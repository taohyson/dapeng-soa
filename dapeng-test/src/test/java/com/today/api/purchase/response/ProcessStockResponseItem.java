/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.today.api.purchase.response;

        import java.util.Optional;
        import com.github.dapeng.org.apache.thrift.TException;
        import com.github.dapeng.org.apache.thrift.protocol.TCompactProtocol;
        import com.github.dapeng.util.TCommonTransport;

        /**
         * Autogenerated by Dapeng-Code-Generator (2.1.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING

        *

 库存处理返回结构体

        **/
        public class ProcessStockResponseItem{
        
            /**
            *

 库存主体: 财务店号或者仓库编号

            **/
            public String ownerId ;
            public String getOwnerId(){ return this.ownerId; }
            public void setOwnerId(String ownerId){ this.ownerId = ownerId; }

            public String ownerId(){ return this.ownerId; }
            public ProcessStockResponseItem ownerId(String ownerId){ this.ownerId = ownerId; return this; }
          
            /**
            *

 库存主体类型: 1:门店(store);2:仓库(warehouse)

            **/
            public com.today.api.stock.enums.StockOwnerTypeEnum ownerType ;
            public com.today.api.stock.enums.StockOwnerTypeEnum getOwnerType(){ return this.ownerType; }
            public void setOwnerType(com.today.api.stock.enums.StockOwnerTypeEnum ownerType){ this.ownerType = ownerType; }

            public com.today.api.stock.enums.StockOwnerTypeEnum ownerType(){ return this.ownerType; }
            public ProcessStockResponseItem ownerType(com.today.api.stock.enums.StockOwnerTypeEnum ownerType){ this.ownerType = ownerType; return this; }
          
            /**
            *

 货号

            **/
            public String skuNo ;
            public String getSkuNo(){ return this.skuNo; }
            public void setSkuNo(String skuNo){ this.skuNo = skuNo; }

            public String skuNo(){ return this.skuNo; }
            public ProcessStockResponseItem skuNo(String skuNo){ this.skuNo = skuNo; return this; }
          
            /**
            *

 当前库存
 @datatype(name="bigdecimal")

            **/
            public java.math.BigDecimal stockNum ;
            public java.math.BigDecimal getStockNum(){ return this.stockNum; }
            public void setStockNum(java.math.BigDecimal stockNum){ this.stockNum = stockNum; }

            public java.math.BigDecimal stockNum(){ return this.stockNum; }
            public ProcessStockResponseItem stockNum(java.math.BigDecimal stockNum){ this.stockNum = stockNum; return this; }
          

        public static byte[] getBytesFromBean(ProcessStockResponseItem bean) throws TException {
          byte[] bytes = new byte[]{};
          TCommonTransport transport = new TCommonTransport(bytes, TCommonTransport.Type.Write);
          TCompactProtocol protocol = new TCompactProtocol(transport);

          new com.today.api.purchase.response.serializer.ProcessStockResponseItemSerializer().write(bean, protocol);
          transport.flush();
          return transport.getByteBuf();
        }

        public static ProcessStockResponseItem getBeanFromBytes(byte[] bytes) throws TException {
          TCommonTransport transport = new TCommonTransport(bytes, TCommonTransport.Type.Read);
          TCompactProtocol protocol = new TCompactProtocol(transport);
          return new com.today.api.purchase.response.serializer.ProcessStockResponseItemSerializer().read(protocol);
        }

        public String toString(){
          StringBuilder stringBuilder = new StringBuilder("{");
            stringBuilder.append("\"").append("ownerId").append("\":\"").append(this.ownerId).append("\",");
    stringBuilder.append("\"").append("ownerType").append("\":").append(this.ownerType).append(",");
    stringBuilder.append("\"").append("skuNo").append("\":\"").append(this.skuNo).append("\",");
    stringBuilder.append("\"").append("stockNum").append("\":").append(this.stockNum).append(",");
    
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
            stringBuilder.append("}");

          return stringBuilder.toString();
        }
      }
      