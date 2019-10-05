sealed trait Trampoline[A]
case class Done[A](value: A) extends Trampoline[A]
case class More[A](call: () => Trampoline[A]) extends Trampoline[A]

def run[A](trampoline: Trampoline[A]): A = {
  trampoline match {
    case Done(v) => v
    case More(t) => run(t())
  }
}

/*
Now we made a description of a program that can be run self-recursively
*/