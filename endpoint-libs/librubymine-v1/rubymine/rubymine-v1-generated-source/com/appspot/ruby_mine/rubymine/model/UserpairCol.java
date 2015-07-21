/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2014-11-17 18:43:33 UTC)
 * on 2014-12-09 at 08:48:39 UTC 
 * Modify at your own risk.
 */

package com.appspot.ruby_mine.rubymine.model;

/**
 * Model definition for UserpairCol.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the rubymine. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class UserpairCol extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer resultCode;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.util.List<Userpair> userpairs;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getResultCode() {
    return resultCode;
  }

  /**
   * @param resultCode resultCode or {@code null} for none
   */
  public UserpairCol setResultCode(java.lang.Integer resultCode) {
    this.resultCode = resultCode;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.util.List<Userpair> getUserpairs() {
    return userpairs;
  }

  /**
   * @param userpairs userpairs or {@code null} for none
   */
  public UserpairCol setUserpairs(java.util.List<Userpair> userpairs) {
    this.userpairs = userpairs;
    return this;
  }

  @Override
  public UserpairCol set(String fieldName, Object value) {
    return (UserpairCol) super.set(fieldName, value);
  }

  @Override
  public UserpairCol clone() {
    return (UserpairCol) super.clone();
  }

}