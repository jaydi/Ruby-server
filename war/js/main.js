// common functions

function replaceAll(find, replace, str) {
    if (find != null && replace != null && str != null)
        return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
    else
        return str;
}

function escapeRegExp(str) {
  return str.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&");
}

function lineBreaks(str) {
    return replaceAll('\n', '<br/>', str)
}

function showPreview(input, where) {
    if (input.files && input.files[0]) {
        var reader = new FileReader();
        reader.onload = function(e) {
            $('#' + where).attr('src', e.target.result);
        };
        reader.readAsDataURL(input.files[0]);
    }
}

function getImageUrl(imageKey) {
    return 'https://ruby-mine.appspot.com/image?blob-key=' + imageKey;
}

// api initialize

var ROOT = 'https://ruby-mine.appspot.com/_ah/api';

function init() {
    gapi.client.load('rubymine', 'v1', function() {
        getRubyzones();
        listenZoneSelect();
        listenMineSelect();
    }, ROOT);
}

// feature functions

function getRubyzones() {
    gapi.client.rubymine.rubyzones.list().execute(function(res) {
        var rubyzones = res.rubyzones || [];
        clearRubyzoneOptions();
        addRubyzoneOptions(rubyzones);
    });
}

function clearRubyzoneOptions() {
    $('#select-zone option').each(function() {
        $(this).remove();
    });
    $('#select-zone').append($('<option></option>').attr('value', 0).text('- -'));
}

function addRubyzoneOptions(rubyzones) {
    $.each(rubyzones, function(key, value) {
        $('#select-zone').append($('<option></option>').attr('value', value.id).text(value.name));
    });
}

function listenZoneSelect() {
    $('#select-zone').change(function(){
        $('#mine-contents-view').html('');
        
        var zoneId = $('#select-zone option:selected').attr('value');
        getRubymines(zoneId);
    });
}

var rubymines;

function getRubymines(zoneId) {
    gapi.client.rubymine.rubymines.list({
        rubyzoneId: zoneId
    }).execute(function(res) {
        rubymines = res.rubymines || [];
        clearRubymineOptions();
        addRubymineOptions(rubymines);
    });
}

function clearRubymineOptions() {
    $('#select-mine option').each(function() {
        $(this).remove();
    });
    $('#select-mine').append($('<option></option>').attr('value', 0).text('- -'));
}

function addRubymineOptions(rubymines) {
    $.each(rubymines, function(key, value) {
        $('#select-mine').append($('<option></option>').attr('value', value.id).text(value.name));
    });
}

var rubymine;
var rubymineContentsObj;
var rubymineContentsIndex;
var mineTextEditing = false;

function listenMineSelect() {
    $('#select-mine').change(function(){
        var mineId = $('#select-mine option:selected').attr('value');
        //console.log('mineId = ' + mineId);
        $.each(rubymines, function(key, value) {
            if (value.id == mineId) {
                rubymine = value;
                //console.log('rubymine = ' + rubymine);
                viewRubymine();
                getMineGems();
            }
        });
    });
}

function viewRubymine() {
    $('#mine-contents-view').html('');
    
    //console.log(rubymine.contents);
    if( rubymine.contents!=undefined ) {
        rubymineContentsObj = JSON.parse(rubymine.contents.value);
        for (var i = 0; i < rubymineContentsObj.length; i++) {
            if (i % 2 == 0)
                viewMineImage(rubymineContentsObj[i]);
            else
                viewMineText(rubymineContentsObj[i], i);
        }
        $('#mine-contents-add-first').css('display', 'inline');
    } else {
        rubymineContentsObj = null;
        $('#mine-contents-add-first').css('display', 'none');
    }
}

function viewMineImage(imageKey) {
    $('#mine-contents-view').append(
        $('<img>').attr('src', getImageUrl(imageKey) + '&width=360&height=0').attr('class', 'mine-image')
    );
}

function viewMineText(text, index) {
    var $div =  $('<div></div>').attr('class', 'mine-button-group')
        .append(
            $('<span>[삭제]</span>').attr('class', 'button').click( function() {
            removeMineItemByTextIdx(index);
            })
        )
        .append(
            $('<span>[아래에 추가]</span>').attr('class', 'button').click( function() {
            addMineItemByTextIdx(index);
            })
        );
    if( index < rubymineContentsObj.length-1 ) {
        $div.append(
            $('<span>[한칸아래로]</span>').attr('class', 'button').click( function() {
            moveMineItemByTextIdx(index, +1);
            })
        );
    }
    if( index > 1 ) {
        $div.append(
            $('<span>[한칸위로]</span>').attr('class', 'button').click( function() {
            moveMineItemByTextIdx(index, -1);
            })
        );
    }
    
    
    
    $('#mine-contents-view').append(
        $('<div></div>').attr('class', 'mine-text').text(text).click(function() {
            editMineText($(this), index);
        })
    ).append(
        $div
    );
}

function moveMineItemByTextIdx(index, stepAmount) {
    var temp0 = rubymineContentsObj[index-1+stepAmount*2];
    var temp1 = rubymineContentsObj[index+stepAmount*2];
    
    rubymineContentsObj[index-1+stepAmount*2] = rubymineContentsObj[index-1];
    rubymineContentsObj[index+stepAmount*2] = rubymineContentsObj[index];
    
    rubymineContentsObj[index-1] = temp0;
    rubymineContentsObj[index] = temp1;
    
    rubymine.contents.value = JSON.stringify(rubymineContentsObj);
    updateRubymine();   
}

function editMineText(obj, index) {
    
    if( mineTextEditing ) return;

    mineTextEditing = true;
    var text = obj.text();
    obj.html('')
        .append(
            $('<textarea></textarea>').text(text)
         )
        .append(
        $('<button></button>').text('수정').click(function(evt) {
            evt.preventDefault();
            
            var newText = $(this).parent().children('textarea').val();
            rubymineContentsObj[index] = newText;
            
            // 서버에 저장
            rubymine.contents.value = JSON.stringify(rubymineContentsObj);
            updateRubymine();
            
            // textarea 없애기
            $(this).parent().html(newText);
            setTimeout(function(){mineTextEditing = false;}, 500);
            
        })
    );
}

function removeMineItemByTextIdx(index) {
    if( confirm("삭제하시겠습니까?")==true ) { 
        // index 부분을 rubymine에서 삭제하고
        rubymineContentsObj.splice(index-1,2);
        // rubymine string을 갱신
        rubymine.contents.value = JSON.stringify(rubymineContentsObj);

        updateRubymine();   
    }
}

function addMineItemByTextIdx(index) {
    //alert(index);
    rubymineContentsIndex = index+1;    // 이 텍스트 바로 아래에 추가하기로.
    
    // mine-editor를 띄우고
    editMineContents(1);
    
    $.get('/html/add-content.html', function(html) {
        // image key를 하나 생성
        $.get('https://ruby-mine.appspot.com/image', function(action) {
            html = replaceAll('${action}', action, html);
            html = replaceAll('${index}', cIndex++, html);
            $('#mine-editor-view').append(html);
            
            // done을 누르면 addMineContentItem
            
        });
    });
    
    
    
    // 
}

function addMineContentItem() {
    
    for (var i = 0; i < cIndex; i++) {
        // 이미지 추가
        rubymineContentsObj.splice(rubymineContentsIndex++, 0,  $('#content-edit-image-' + i).data('key'));
        rubymineContentsObj.splice(rubymineContentsIndex++, 0,  $('#content-edit-text-' + i).val());
    }
    
    // rubymine string을 갱신
    rubymine.contents.value = JSON.stringify(rubymineContentsObj);

    updateRubymine();   
    cancelMineEdit();
}

function updateRubymine() {
    // 화면에 다시 표시
    viewRubymine();
    
    // 서버에 갱신
    gapi.client.rubymine.rubymines.update(rubymine).execute( function(res) {
        //alert(res);
    });
}

var gems;

function getMineGems() {
    $('#mine-gem-view').html('');
    console.log('rubymine id = ' + rubymine.id);
    
    gapi.client.rubymine.gems.list.mine({
        rubymineId: rubymine.id
    }).execute(function(res) {
        gems = res.gems || [];
        viewGems(gems);
    });
}

function viewGems(gems) {
    $.get('/html/gem.html', function(html) {
        var i = 0;
        $.each(gems, function(key, gem) {
            $.get('https://ruby-mine.appspot.com/image', function(action) {
                var gemHtml = html.replace('${imageUrl}', getImageUrl(gem.imageKey) + '&width=180&height=240');
                gemHtml = gemHtml.replace('${action}', action);
                gemHtml = replaceAll('${index}', i, gemHtml);
                gemHtml = gemHtml.replace('${name}', gem.name);
                gemHtml = gemHtml.replace('${value}', gem.value);
                gemHtml = gemHtml.replace('${ea}', gem.ea);
                $gemDiv = $(gemHtml);
                $gemDiv.find('.gem-remove').click(function() {
                    removeGem(key, gem.id);
                });
                $('#mine-gem-view').append($gemDiv);
                i++;
            });
        });
    });
}

function removeGem(index, gemId) {
    // 서버 갱신
    gapi.client.rubymine.gems.delete({
        id:gemId
    }).execute(function(res) {
        console.log(res);
        getMineGems();
    });
}

var cIndex;

function editMineContents(type) {
    $('#mine-viewer').css('display', 'none');
    $('#mine-editor').css('display', 'block');
    
    switch(type) {
            case 0:
            $('#mine-contents-add').css('display', 'inline');
            $('#mine-contents-save').css('display', 'inline');
            $('#mine-contents-cancel').css('display', 'inline');
            $('#mine-contents-done').css('display', 'none');
            break;
            case 1:
            $('#mine-contents-add').css('display', 'none');
            $('#mine-contents-save').css('display', 'none');
            $('#mine-contents-cancel').css('display', 'inline');
            $('#mine-contents-done').css('display', 'inline');
            break;
    }
    
    cIndex = 0;
}

function addMineContent() {
    $.get('/html/add-content.html', function(html) {
        $.get('https://ruby-mine.appspot.com/image', function(action) {
            html = replaceAll('${action}', action, html);
            html = replaceAll('${index}', cIndex++, html);
            $('#mine-editor-view').append(html);
        });
    });
}

function changeContentImage(index, input, where) {
    $('#content-edit-image-progress-' + index).css('display', 'block');
    $('#content-edit-form-' + index).ajaxSubmit(function(res) {
    $('#content-edit-image-progress-' + index).css('display', 'none');
        $('#content-edit-image-' + index).data('key', res);
        showPreview(input, where);
    });
}

function cancelMineEdit() {
    $('#mine-editor-view').html('');
    $('#mine-editor').css('display', 'none');
    $('#mine-viewer').css('display', 'block');
}

function saveMineContents() {
    var mineContents = [];
    
    for (var i = 0; i < cIndex; i++) {
        mineContents[mineContents.length] = $('#content-edit-image-' + i).data('key');
        mineContents[mineContents.length] = $('#content-edit-text-' + i).val();
    }
    
    sendMineContents(mineContents);
}

function sendMineContents(mineContents) {
    var contentsString = JSON.stringify(mineContents);
    rubymine.contents = { value: contentsString };
    gapi.client.rubymine.rubymines.update(rubymine).execute(function(res) {
        cancelMineEdit();
        rubymine = res;
        viewRubymine();
    });
}

var gIndex;

function editMineGems() {
    $('#mine-gem-viewer').css('display', 'none');
    $('#mine-gem-editor').css('display', 'block');
    gIndex = 0;
    
    addMineGem();
}

function addMineGem() {
    $.get('/html/add-gem.html', function(html) {
        $.get('https://ruby-mine.appspot.com/image', function(action) {
            html = replaceAll('${action}', action, html);
            html = replaceAll('${index}', gIndex++, html);
            $('#mine-gem-editor-view').append(html);
        });
    });
}

function addGemImage(index, input, where) {
    $('#gem-edit-image-progress-' + index).css('display', 'block');
    $('#gem-edit-form-' + index).ajaxSubmit(function(res) {
    $('#gem-edit-image-progress-' + index).css('display', 'none');
        $('#gem-edit-image-' + index).data('key', res);
        showPreview(input, where);
    });
}

function cancelGemEdit() {
    $('#mine-gem-editor-view').html('');
    $('#mine-gem-editor').css('display', 'none');
    $('#mine-gem-viewer').css('display', 'block');
}

function saveMineGems() {
    var mineGems = [];
    
    for (var i = 0; i < gIndex; i++) {
        mineGems[mineGems.length] = {
            rubymineId: rubymine.id,
            name: $('#gem-edit-name-' + i).val(),
            rubymineName: rubymine.name,
            imageKey: $('#gem-edit-image-' + i).data('key'),
            desc: $('#gem-edit-desc-' + i).val(),
            type: 0,
            value: $('#gem-edit-value-' + i).val(),
            ea: $('#gem-edit-ea-' + i).val()
        };
    }
    
    sendMineGems(mineGems);
}

function sendMineGems(gems) {
    var uc = 0;
    $.each(gems, function(key, gem) {
        gapi.client.rubymine.gems.insert(gem).execute(function(res) {
            console.log("uc="+uc+", gIndex="+gIndex);
            if (++uc == gIndex) {
                cancelGemEdit();
                getMineGems();
            }
        });
    });
}

function changeGemImage(index, input, where) {
    $('#gem-image-progress-' + index).css('display', 'block');
    $('#gem-image-form-' + index).ajaxSubmit(function(res) {
        $('#gem-image-progress-' + index).css('display', 'none');
        showPreview(input, where);
        
        var gem = gems[index];
        gem.imageKey = res;
        editGem(gem);
    });
}

function editGem(gem) {
    gapi.client.rubymine.gems.edit(gem).execute(function(res) {
        getMineGems();
    });
}




























