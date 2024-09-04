package com.example.shoppingapp.helper

fun Float?.getProductPrice(price:Float):Float{
  //this --> offer percentage
  if(this == null)
    return price
  val leftPercent = 1f - this
  val priceAfterOffer = leftPercent *price
  return priceAfterOffer

}