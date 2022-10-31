package dev.danilo.simplecalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    // Numbers
    private var btnOne: Button? = null
    private var btnTwo: Button? = null
    private var btnThree: Button? = null
    private var btnFour: Button? = null
    private var btnFive: Button? = null
    private var btnSix: Button? = null
    private var btnSeven: Button? = null
    private var btnEight: Button? = null
    private var btnNine: Button? = null
    private var btnZero: Button? = null

    // Operators
    private var btnDivide: Button? = null
    private var btnMultiply: Button? = null
    private var btnAdd: Button? = null
    private var btnSubtract: Button? = null

    // Others
    private var btnAC: Button? = null
    private var btnBin: Button? = null
    private var btnDec: Button? = null
    private var btnDot: Button? = null
    private var btnDelete: Button? = null
    private var btnEqual: Button? = null
    private var tvRightBottom: TextView? = null
    private var tvWarning: TextView? = null
    private var tvPreviousEquation: TextView? = null
    private val operators = listOf('+', '-', '*', '/')
    private val valueIsTooBig = "VALUE IS TOO BIG!"
    private val invalidBinary = "NOT A BINARY NUMBER!"
    private val floatingPointImprecisionWarning = "PRONE TO FLOATING POINT IMPRECISION!"
    private val cantConvertToBinaryWarning = "CAN'T CONVERT TO BINARY!"
    private var maxAmountOfDigits = 15
    private var powersOfTwo = listOf(1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, 32768)

    // Flags
    private var lastNumber = false
    private var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initiating variables
        tvRightBottom = findViewById(R.id.tvRightBottom)


        // Numbers
        btnOne = findViewById(R.id.btnOne)
        btnTwo = findViewById(R.id.btnTwo)
        btnThree = findViewById(R.id.btnThree)
        btnFour = findViewById(R.id.btnFour)
        btnFive = findViewById(R.id.btnFive)
        btnSix = findViewById(R.id.btnSix)
        btnSeven = findViewById(R.id.btnSeven)
        btnEight = findViewById(R.id.btnEight)
        btnNine = findViewById(R.id.btnNine)
        btnZero = findViewById(R.id.btnZero)

        // Operators
        btnDivide = findViewById(R.id.btnDivide)
        btnMultiply = findViewById(R.id.btnMultiply)
        btnAdd = findViewById(R.id.btnAdd)
        btnSubtract = findViewById(R.id.btnSubtract)

        // Others
        btnAC = findViewById(R.id.btnClear)
        btnBin = findViewById(R.id.btnBin)
        btnDec = findViewById(R.id.btnDec)
        btnDot = findViewById(R.id.btnDot)
        btnDelete = findViewById(R.id.btnDelete)
        btnEqual = findViewById(R.id.btnEqual)
        tvWarning = findViewById(R.id.tvWarning)
        tvPreviousEquation = findViewById(R.id.tvPreviousEquation)


        // Setting event listeners

        // Numbers
        btnOne?.setOnClickListener { onNumber('1') }
        btnTwo?.setOnClickListener { onNumber('2') }
        btnThree?.setOnClickListener { onNumber('3') }
        btnFour?.setOnClickListener { onNumber('4') }
        btnFive?.setOnClickListener { onNumber('5') }
        btnSix?.setOnClickListener { onNumber('6') }
        btnSeven?.setOnClickListener { onNumber('7') }
        btnEight?.setOnClickListener { onNumber('8') }
        btnNine?.setOnClickListener { onNumber('9') }
        btnZero?.setOnClickListener { onNumber('0') }

        // Operators
        btnDivide?.setOnClickListener { onOperator('/') }
        btnMultiply?.setOnClickListener { onOperator('*') }
        btnAdd?.setOnClickListener { onOperator('+') }
        btnSubtract?.setOnClickListener { onOperator('-') }

        // Others
        btnAC?.setOnClickListener { onClear() }
        btnBin?.setOnClickListener { toBinary() }
        btnDec?.setOnClickListener { toDecimal() }
        btnDot?.setOnClickListener { onDecimalPoint() }
        btnDelete?.setOnClickListener { onDelete() }
        btnEqual?.setOnClickListener { onEqual() }
    }

    private fun fitNumber(number: String): String {
        if (number.length > maxAmountOfDigits) {
            tvWarning?.let {
                it.text = valueIsTooBig
                it.visibility = View.VISIBLE
            }
            return number.subSequence(0, maxAmountOfDigits).toString()
        }
        return number
    }

    private fun numberFits(): Boolean {
        if (tvRightBottom?.text?.length!! < maxAmountOfDigits) { return true }
        tvWarning?.let {
            it.text = valueIsTooBig
            it.visibility = View.VISIBLE
        }
        return false
    }

    private val removeLeadingMinusIfAny = { string: String ->
        if (string[0] == '-') string.drop(1) else string }

    private fun onNumber(value: Char) {
        if (!numberFits()) { return }
        tvWarning?.visibility = View.INVISIBLE
        tvRightBottom?.append(value.toString())
        lastNumber = true
        lastDot = false
    }

    private fun onOperator(value: Char) {
        if (!numberFits()) { return }
        if (!hasOperator() && !lastDot) {
            tvRightBottom?.let {
                if (it.text.isNotEmpty() || value == '-') { it.append(value.toString()) }
                lastNumber = false
                lastDot = false
            }
        }
    }

    private fun hasOperator(): Boolean {
        tvRightBottom?.text?.let {
            if (it.any()) {
                // Prevent person from using "-/", "-*"...
                if (it.length == 1 && it[0] == '-') { return true }

                val myText = removeLeadingMinusIfAny(it.toString())
                operators.forEach { operator ->
                    if (myText.contains(operator)) return true
                }
            }
        }
        return false
    }

    private fun onClear() {
        tvPreviousEquation?.text = ""
        tvWarning?.visibility = View.INVISIBLE
        tvRightBottom?.text = ""
        lastNumber = false
        lastDot = false
    }

    private fun toBinary() {
        tvRightBottom?.let {
            if (!it.text.any()) { return }
            tvWarning?.visibility = View.INVISIBLE
            it.text.forEach { char ->
                if (!char.isDigit()) {
                    tvWarning?.let { warning ->
                        warning.visibility = View.VISIBLE
                        warning.text = cantConvertToBinaryWarning
                        return
                    }
                }
            }

            var remainders = ""
            var number = it.text.toString().toLong()
            while (number >= 2) {
                remainders = remainders.plus(number % 2)
                number /= 2
            }
            val myText = "${it.text} to binary"
            tvPreviousEquation?.text = myText
            val result = remainders.plus(number).reversed()
            it.text = fitNumber(result)
        }
    }

    private fun toDecimal() {
        var myBinary = ""
        tvRightBottom?.let {
            myBinary = it.text.toString().reversed()
            tvWarning?.visibility = View.INVISIBLE
            it.text.forEach { char ->
                if (char != '0' && char != '1') {
                    tvWarning?.let { warning ->
                        warning.text = invalidBinary
                        warning.visibility = View.VISIBLE
                        return
                    }
                }
            }
            var result: Long = 0
            myBinary.forEachIndexed { index, char ->
                if (char == '1') { result += powersOfTwo[index] }
            }
            val myText = "${it.text} to decimal"
            tvPreviousEquation?.text = myText
            it.text = fitNumber(result.toString())
        }
    }

    private fun onDecimalPoint() {
        tvWarning?.visibility = View.INVISIBLE
        if (!numberFits()) { return }
        tvRightBottom?.let {
            if (!hasDecimalPoint()) {
                it.append('.'.toString())
                lastDot = true
                lastNumber = false
            }
        }
    }

    private fun hasDecimalPoint(): Boolean {
        tvRightBottom?.text?.let {
            if (it.isNotEmpty()) {
                val myText = removeLeadingMinusIfAny(it.toString())
                val myList = mutableListOf<String>()
                for (operator in operators) {
                    if (myText.contains(operator)) {
                        myList.addAll(it.split(operator))
                        break
                    }
                }
                if (myList.isEmpty()) { myList.add(myText) }
                val myExpression = myList.last()
                if (myExpression.contains('.')) return true
            }
        }
        return false
    }

    private fun onDelete() {
        tvPreviousEquation?.text = ""
        tvRightBottom?.let {
            tvWarning?.visibility = View.INVISIBLE
            lastDot = false
            lastNumber = false

            it.text = it.text?.dropLast(1)
            if (it.text.any()) {
                val lastChar = it.text.last()
                if (lastChar.isDigit()) { lastNumber = true }
                else if (lastChar == '.') { lastDot = true }
            }
        }
    }

    private fun onEqual() {
        if (!hasOperator()) { return }
        tvRightBottom?.let {
            val myList = mutableListOf<String>()
            var firstNumberIsNegative = false
            val myText = removeLeadingMinusIfAny(it.text.toString())
            if (myText != it.text.toString()) { firstNumberIsNegative = true }
            var operator = ' '
            for (op in operators) {
                if (myText.contains(op))  {
                    myList.addAll(myText.split(op))
                    operator = op
                    break
                }
            }
            if (myList.size < 2 || myList.last().isEmpty()) { return }
            var n1 = myList[0]
            val n2 = myList[1]
            var resultLong: Long? = null
            var resultDouble: Double? = null

            if (firstNumberIsNegative) { n1 = "-$n1" }

            when (operator) {
                '+' -> {
                    if (n1.contains('.') && n2.contains('.')) { resultDouble = n1.toDouble() + n2.toDouble() }
                    else if (!n1.contains('.') && !n2.contains('.')) { resultLong = n1.toLong() + n2.toLong() }
                    else if (n1.contains('.') && !n2.contains('.')) { resultDouble = n1.toDouble() + n2.toLong() }
                    else if (!n1.contains('.') && n2.contains('.')) { resultDouble = n1.toLong() + n2.toDouble() }
                }
                '-' -> {
                    if (n1.contains('.') && n2.contains('.')) { resultDouble = n1.toDouble() - n2.toDouble() }
                    else if (!n1.contains('.') && !n2.contains('.')) { resultLong = n1.toLong() - n2.toLong() }
                    else if (n1.contains('.') && !n2.contains('.')) { resultDouble = n1.toDouble() - n2.toLong() }
                    else if (!n1.contains('.') && n2.contains('.')) { resultDouble = n1.toLong() - n2.toDouble() }
                }
                '*' -> {
                    if (n1.contains('.') && n2.contains('.')) { resultDouble = n1.toDouble() * n2.toDouble() }
                    else if (!n1.contains('.') && !n2.contains('.')) { resultLong = n1.toLong() * n2.toLong() }
                    else if (n1.contains('.') && !n2.contains('.')) { resultDouble = n1.toDouble() * n2.toLong() }
                    else if (!n1.contains('.') && n2.contains('.')) { resultDouble = n1.toLong() * n2.toDouble() }
                }
                '/' -> {
                    if (n2.toDouble() == 0.0) { return }
                    if (n1.contains('.') && n2.contains('.')) { resultDouble = n1.toDouble() / n2.toDouble() }
                    else if (!n1.contains('.') && !n2.contains('.')) {
                        resultDouble = if (n1.length > n2.length) {
                            n1.toLong() / n2.toDouble()
                        } else {
                            n1.toDouble() / n2.toLong()
                        }
                    }
                    else if (n1.contains('.') && !n2.contains('.')) { resultDouble = n1.toDouble() / n2.toLong() }
                    else if (!n1.contains('.') && n2.contains('.')) { resultDouble = n1.toLong() / n2.toDouble() }
                }
            }

            tvPreviousEquation?.text = fitNumber(it.text.toString())
            if (resultLong != null) { it.text = fitNumber(resultLong.toString()) }
            else if (resultDouble != null) {
                tvWarning?.let { warning ->
                    val afterDecimal = resultDouble.toString().substringAfter('.')
                    var onlyZerosAfterDecimal = true
                    var numbersAfterDecimal = 0
                    for (char in afterDecimal) {
                        if (char != '0') { onlyZerosAfterDecimal = false }
                        numbersAfterDecimal++
                    }

                    if (onlyZerosAfterDecimal) {
                        it.text = resultDouble.toString().substringBefore('.')
                        return
                    }

                    it.text = fitNumber(resultDouble.toString())

                    if (operator == '/' && numbersAfterDecimal > 5) {
                        warning.text = floatingPointImprecisionWarning
                        warning.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}