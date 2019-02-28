import scala.util.Try

sealed trait Expr[T] { //T could be String
  /**
    * Pass a function that tells us how to handle each type
    * Folding replaces all the instructions in the program with instructions in a different language
    * @param lit
    * @param and
    * @param many
    * @param or
    * @tparam A
    * @return
    */
  def fold[A](lit: T => A,
              and: (A, A) => A,
              many: A => A,
              or: (A, A) => A
             ): A = {
    def go(x: Expr[T]): A = x match {
      case Lit(s) => lit(s)
      case And(a, b) => and(go(a), go(b))
      case Many(e) => many(go(e))
      case Or(a, b) => or(go(a), go(b))
    }
    go(this)
  }
}

case class Lit[A](a: A) extends Expr[A]
case class Many[T](a: Expr[T]) extends Expr[T]
case class Or[T](a: Expr[T], a2: Expr[T]) extends Expr[T]
case class And[T](a: Expr[T], a2: Expr[T]) extends Expr[T]

def booleanInterpreter(x: Expr[Boolean]): Boolean = x.fold[Boolean](identity[Boolean], _&&_, !_, _||_)
println(s"booleanInterpreter ${booleanInterpreter(And(Lit(true), Lit(true)))}")
println(s"booleanInterpreter ${booleanInterpreter(And(Lit(true), Lit(false)))}")
println(s"booleanInterpreter ${booleanInterpreter(Or(Lit(true), Lit(true)))}")

case class RegexRes(restOfString: String, valid: Boolean)
def simpleRegexInterpreter(x: Expr[String], s: String): RegexRes = x.fold[RegexRes](
  lit = t => {
    val splited = s.split(t)
    if(splited.size > 1 || s == t) RegexRes(Try(splited(1)).getOrElse(s), true) else RegexRes(s, false)
    // t == Hi or t == Bye grammer. Input string is???
  },
  and = (a, b) => if (a.valid && b.valid) RegexRes(b.restOfString, true) else RegexRes(b.restOfString, false),
  or = (a, b) => if (a.valid || b.valid) RegexRes(b.restOfString, true) else RegexRes(b.restOfString, false),
  many = a => a //TODO do this
)


val hiLit = Lit("Hi")
val byeLit = Lit("Bye")
val hiOrBye = Or(hiLit, byeLit)
val hiAndBye = And(hiLit, byeLit)


println(" simple regex" + simpleRegexInterpreter(hiLit, "Hi"))
println(" simple regex" + simpleRegexInterpreter(hiLit, "HiBye"))
println(" simple regex" + simpleRegexInterpreter(hiAndBye, "HiBye"))
println(" simple regex" + simpleRegexInterpreter(hiOrBye, "HiBye"))

