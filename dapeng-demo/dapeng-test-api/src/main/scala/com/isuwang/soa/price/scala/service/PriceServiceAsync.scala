
      package com.isuwang.soa.price.scala.service

      import com.github.dapeng.core.{Processor, Service}
      import com.github.dapeng.core.SoaGlobalTransactional
      import scala.concurrent.Future

      /**
       * Autogenerated by Dapeng-Code-Generator (1.2.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated

      * 
      **/
      @Service(name ="com.isuwang.soa.price.service.PriceService" , version = "1.0.0")
      @Processor(className = "com.isuwang.soa.price.scala.PriceServiceAsyncCodec$Processor")
      trait PriceServiceAsync extends com.github.dapeng.core.definition.AsyncService {
      
          /**
          * 
          **/
          
          @throws[com.github.dapeng.core.SoaException]
          def insertPrice(
          price: com.isuwang.soa.price.scala.domain.Price ): Future[Unit]

        
          /**
          * 
          **/
          
          @throws[com.github.dapeng.core.SoaException]
          def getPrices(
          ): Future[List[com.isuwang.soa.price.scala.domain.Price]]

        
    }
    