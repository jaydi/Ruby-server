package com.jaydi.ruby.apis.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.jaydi.ruby.models.PMF;
import com.jaydi.ruby.models.Receipt;
import com.jaydi.ruby.models.ReceiptResponse;

public class ReceiptServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ReceiptServlet.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String menuData = req.getParameter("menuData");
		long shopId = Long.valueOf(req.getParameter("shopId"));
		int totAmount = Integer.valueOf(req.getParameter("totAmount"));
		int rubyValue = 1 + (totAmount / 4000);

		Receipt receipt = new Receipt();
		receipt.setMenus(menuData);
		receipt.setShopId(shopId);
		receipt.setPrice(totAmount);
		receipt.setCreatedAt(new Date().getTime());

		PersistenceManager pm = PMF.getPersistenceManager();
		Query query = pm.newQuery(Receipt.class);
		query.setOrdering("id desc");
		query.setRange(0, 1);
		List<Receipt> exs = (List<Receipt>) pm.newQuery(query).execute();
		if (!exs.isEmpty())
			receipt.setId(exs.get(0).getId() + 1l);
		else
			receipt.setId(1l);

		pm.makePersistent(receipt);
		pm.close();

		log.info("receipt: " + receipt.getShopId() + "/" + receipt.getPrice() + "/" + receipt.getMenus());

		ReceiptResponse response = new ReceiptResponse();
		response.setResult("ok");
		response.setCode(200);
		response.setMessage("valid code");
		response.setCodeInfo("9f.vc/r.asp?r=" + receipt.getId());
		response.setNumCheckin(1);
		response.setInfoTitle("아래 QR코드를 촬영하면 루비가 획득됩니다!\r\n");
		response.setInfoPostScript("[루비 " + rubyValue + "개]");
		response.setInfoReward("루비는 메가박스를 비롯한 서현역 상점의 식사,커피,팝콘 등 다양한 상품으로 교환가능한 포인트입니다.");

		String jsonRes = makeJsonResponse(response);

		log.info("response: " + jsonRes);

		res.setContentType("application/json; charset=UTF-8");
		PrintWriter pw = res.getWriter();
		pw.write(jsonRes);
		pw.flush();
		pw.close();
	}

	private String makeJsonResponse(ReceiptResponse response) {
		return new Gson().toJson(response);
	}
}
