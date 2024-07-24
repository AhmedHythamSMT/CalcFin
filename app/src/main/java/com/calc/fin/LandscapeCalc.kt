package com.calc.fin

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

class LandscapeCalc : AppCompatActivity() {

    private var canAddOperation = false
    private var canAddDecimal = true

    private lateinit var memory: TextView
    private lateinit var mPlus: Button
    private lateinit var mMines: Button
    private lateinit var mR: Button
    private lateinit var mC: Button
    private var memoryValue = 0.0
    private lateinit var editViewer: TextView
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landscape_calc)


        editViewer = findViewById(R.id.editView)
        memory = findViewById(R.id.memory)
        mPlus = findViewById(R.id.mPLUS)
        mMines = findViewById(R.id.mMINES)
        mR = findViewById(R.id.mr)
        mC = findViewById(R.id.mc)



        //add mem value to the current number
        mPlus.setOnClickListener{
            memoryValue += ResultViewer.text.toString().toDouble()
            memory.text = "M: $memoryValue"
        }
        //subtract mem value from the current number
        mMines.setOnClickListener{
            memoryValue -= ResultViewer.text.toString().toDouble()
            memory.text = "M: $memoryValue"
        }
        //recall from memory
        mR.setOnClickListener{
            editView.text = memoryValue.toString()
        }
        //clear memory
        mC.setOnClickListener{
            memoryValue = 0.0
            memory.text = "M: 0.0"
        }

    }
    // On screen orientation method
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {


        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait mode
            val intent = Intent (this, MainActivity::class.java)


            handler.postDelayed({startActivity(intent)},1)
        }
    }

    //when press on number buttons
    fun numAction(view: View) {
        if(view is Button){
            if (view.text == "."){
                if (canAddDecimal){
                    editView.append(view.text)
                    canAddDecimal= false

                }
            }else
                editView.append(view.text)
            canAddOperation = true

        }
    }
    //when press on Operations buttons
    fun OperationAction(view: View) {
        if(view is Button && canAddOperation){
            editView.append(view.text)
            canAddOperation = false
            canAddDecimal = true
        }
    }
    //when press on clear button
    fun onAllClear(view: View) {
        editView.text = ""
        ResultViewer.text = "0"
    }

    //when press on backspace button
    fun backspace(view: View) {
        val len = editView.length()
        if(len  > 0){
            editView.text = editView.text.subSequence(0,len-1)

        }
    }
    //when press on equal button
    fun equalAction(view: View) {
        ResultViewer.text = calcRes()
    }
    //func Result
    private fun calcRes(): String {
        val digOperators = digOperators(editViewer)
        if (digOperators.isEmpty())  return ""

        val timesDivision = timesDivisionCalc(digOperators)
        if (timesDivision.isEmpty())  return ""

//        val sinCosTanCalc = sinCosTanCalc(timesDivision)
//        if (sinCosTanCalc.isNaN())  return ""

        val result = addSubtractCalc(timesDivision)
        return result.toString()

    }

    //func calculate plus mines and modulus and return result
    private fun addSubtractCalc(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for(i in passedList.indices){
            if (passedList [i] is Char && i != passedList.lastIndex){
                val operator = passedList[i]
                val nxtDig = passedList[i + 1] as Float
                if ( operator == '+')
                    result += nxtDig
                if ( operator == '-')
                    result -= nxtDig
                if(operator == '%')
                    result %= nxtDig
            }

        }
        return result
    }
    //func take mutable list to multiply and divide and return result
    private fun timesDivisionCalc(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('x') || list.contains('÷'))
        {
            list = calcTimesDiv(list)
        }
        return list

    }
    //func arrange digit operators and return list
    private fun digOperators(editViewer: TextView): MutableList<Any> {
        val list = mutableListOf<Any>()
        var curDig = ""
        val editView = editViewer.text
        for (character in editView){
            if(character.isDigit() || character == '.')
                curDig += character
            else
            {
                list.add(curDig.toFloat())
                curDig = ""
                list.add(character)
            }
        }
        if (curDig != "")
            list.add(curDig.toFloat())

        return list
    }
    //func calculate multiply adn divide and return result
    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newlist = mutableListOf<Any>()
        var restartIndex = passedList.size

        for ( i in passedList.indices){
            if (passedList [i] is Char && i != passedList.lastIndex && i < restartIndex)
            {
                val operator = passedList[i]
                val prvDig = passedList[i - 1] as Float
                val nxtDig = passedList[i + 1] as Float
                when(operator) {
                    'x' -> {
                        newlist.add(prvDig * nxtDig)
                        restartIndex = i + 1
                    }
                    '÷' -> {
                        newlist.add(prvDig / nxtDig)

                        restartIndex = i + 1
                    }
                    else -> {
                        newlist.add(prvDig)
                        newlist.add(operator)
                    }
                }
            }
            if( i > restartIndex)
                newlist.add(passedList[i])
        }

        return newlist

    }
    //    private fun sinCosTanCalc(passedList: MutableList<Any>): Float {
////        var result = passedList[0] as Float
////        val angleInRadians = readLine()?.toDoubleOrNull()
////
////        for(i in passedList.indices){
////            if (passedList [i] is Char && i != passedList.lastIndex){
////                val operator = passedList[i]
////                val nxtDig = passedList[i + 1] as Float
////                if ( operator == "sin"){
////                    if (angleInRadians != null) {
////                        val sinValue = sin(angleInRadians)
////                        result = sinValue.toFloat()
////                } else {
////                    println("Invalid input!")
////                }
////            }
////
////                if ( operator == "cos") {
////                    if (angleInRadians != null) {
////                        val cosValue = cos(angleInRadians)
////                        result = cosValue.toFloat()
////                    } else {
////                        println("Invalid input!")
////                    }
////                }
////                if(operator == "tan")
////                    if (angleInRadians != null) {
////                        val tanValue = tan(angleInRadians)
////                        result = tanValue.toFloat()
////                    } else {
////                        println("Invalid input!")
////                    }
////            }
////        }
////        return result as Float
////        }
//    }
}
