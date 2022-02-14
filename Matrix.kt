import java.util.*

//---------------class Matrix------------------------------------------------------------
class Matrix(val nRows: Int, val nCols: Int, val ini: (Int, Int) -> Double) {
    val d = MutableList(nRows) { i -> MutableList<Double>(nCols) { j -> ini(i, j) } }

    fun printData() { d.forEach { it -> println(it) } }

    fun readData() {
        repeat(nRows) { r -> d[r] = readLine()!!.split(" ").map {it -> it.toDouble()}.toMutableList() }
    }

    operator fun plus(m: Matrix) = Matrix(nRows, nCols) {i, j -> d[i][j] + m.d[i][j]}

    operator fun times(const: Double) = Matrix(nRows, nCols) {i, j -> d[i][j] * const}

    operator fun times(m: Matrix) = Matrix(nRows, m.nCols) {i, j ->
        var sum = 0.0
        for (k in 0 until m.nCols) sum += d[i][k] * m.d[k][j]
        sum
    }

    fun transposeM() = Matrix(nRows, nCols) {i, j -> d[j][i]}
    fun transposeS() = Matrix(nRows, nCols) {i, j -> d[nRows - 1 - j][nCols - 1 - i]}
    fun transposeV() = Matrix(nRows, nCols) {i, j -> d[i][nCols - 1 - i]}
    fun transposeH() = Matrix(nRows, nCols) {i, j -> d[nRows - 1 - j][i]}

    fun determinant() = Matrix(1, 1) { _, _ -> determ()}

    fun algebraicAdd() = Matrix(nRows, nCols) {i, j -> minor(i, j).determ() * if((i + j) % 2 == 0) 1 else -1}

    fun determ(): Double {
        if (d.size == 1) return d[0][0]
        var sum = 0.0
        repeat(d.size) { j -> sum += minor(0, j).determ() * d[0][j] * if(j % 2 == 0) 1 else -1 }
        return sum
    }

    fun minor(k: Int, l: Int) = Matrix(nRows - 1, nCols - 1) {i, j -> d[if (i < k) i else i + 1][if (j < l) j else j + 1]
    }

    fun roundMatrix() = Matrix(nRows, nCols) {i, j ->
        val s = String.format("%.2f", d[i][j])
        s.toDouble()
    }

}

fun selectMenu(): String {
    while(true) {
        println("1. Add matrices")
        println("2. Multiply matrix by a constant")
        println("3. Multiply matrices")
        println("4. Transpose matrix")
        println("5. Calculate a determinant")
        println("6. Inverse matrix")
        println("0. Exit")
        val s = readLine()!!
        if (s.matches(Regex("[0-6]"))) return s
    }
}

fun selectMenuTranspose(): String {
    println("1. Main diagonal")
    println("2. Side diagonal")
    println("3. Vertical line")
    println("4. Horizontal line")
    val s = readLine()!!
    if (s.matches(Regex("[1-4]"))) return s
    return ""
}

fun createMatrix(msg: String): Matrix {
    println("Enter matrix size:")
    val (n, m) = readLine()!!.split(" ").map {it -> it.toInt()}
    val mat = Matrix(n, m) {i, j, -> 0.0}
    println(msg)
    mat.readData()
    return mat
}

//---------------main--------------------------------------------------------------------
fun main() {
    val locale = Locale("ENGLISH")
    Locale.setDefault(locale)

    while(true) {
        var result = Matrix(1, 1) { i, j -> 0.0 }
        when(selectMenu()) {
            "0" -> break
            "1" -> {
                val m1 = createMatrix("Enter first matrix:")
                val m2 = createMatrix("Enter second matrix:")
                result = m1 + m2
            }
            "2" -> {
                val m1 = createMatrix("Enter matrix:")
                println("Enter constant:")
                val const = readLine()!!.toDouble()
                result = m1 * const
            }
            "3" -> {
                val m1 = createMatrix("Enter first matrix:")
                val m2 = createMatrix("Enter second matrix:")
                result = m1 * m2
            }
            "4" -> {
                val s1 = selectMenuTranspose()
                if (s1 =="") continue
                val m1 = createMatrix("Enter matrix:")
                when(s1) {
                    "1" -> { result = m1.transposeM()}
                    "2" -> { result = m1.transposeS()}
                    "3" -> { result = m1.transposeV()}
                    "4" -> { result = m1.transposeH()}
                }
            }
            "5" -> {
                val m1 = createMatrix("Enter matrix:")
                result = m1.determinant()
            }
            "6" -> {
                val m1 = createMatrix("Enter matrix:")
                val detA = m1.determ()
                if (detA == 0.0) {
                    println("This matrix doesn't have an inverse.")
                    continue
                }
                result = m1.algebraicAdd().transposeM().times(1 / detA).roundMatrix()
            }
        }
        println("The result is:")
        result.printData()
    }
}