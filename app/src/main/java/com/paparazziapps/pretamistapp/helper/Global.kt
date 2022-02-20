package com.paparazziapps.pretamistapp.helper

import java.text.DecimalFormat


fun getDoubleWithTwoDecimals (number:Double):String?
{
    return DecimalFormat("###,###,###.00").format(number)
}