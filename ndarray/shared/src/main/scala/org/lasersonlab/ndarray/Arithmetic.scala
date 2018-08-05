package org.lasersonlab.ndarray

import org.hammerlab.shapeless.tlist._
import shapeless.Lazy

trait Arithmetic[L, R] {
  def   + ( l: L, r: R ): L
  def   - ( l: L, r: R ): L
  def   * ( l: L, r: R ): L
  def   / ( l: L, r: R ): L
  def   % ( l: L, r: R ): L
  def min ( l: L, r: R ): L
}
object Arithmetic {
  type Id[T] = Arithmetic[T, T]
  implicit val intint: Id[Int] =
    new Arithmetic[Int, Int] {
      def   + (l: Int, r: Int): Int = l + r
      def   - (l: Int, r: Int): Int = l - r
      def   * (l: Int, r: Int): Int = l * r
      def   / (l: Int, r: Int): Int = l / r
      def   % (l: Int, r: Int): Int = l % r
      def min (l: Int, r: Int): Int = Math.min(l, r)
    }

  implicit def tnil[R]: Arithmetic[TNil, R] =
    new Arithmetic[TNil, R] {
      def   + (l: TNil, r: R): TNil = TNil
      def   - (l: TNil, r: R): TNil = TNil
      def   * (l: TNil, r: R): TNil = TNil
      def   / (l: TNil, r: R): TNil = TNil
      def   % (l: TNil, r: R): TNil = TNil
      def min (l: TNil, r: R): TNil = TNil
    }

  implicit def cons[
    H,
    T <: TList,
    R
  ](
    implicit
    headArithmetic: Lazy[Arithmetic[H, R]],
    tailArithmetic: Lazy[Arithmetic[T, R]],
    pp: Prepend[H, T]
  ):
    Arithmetic[
      H :: T,
      R
    ] =
    new Arithmetic[H :: T, R] {
      implicit val head = headArithmetic.value
      implicit val tail = tailArithmetic.value
      def  +  (l: H :: T, r: R): H :: T = ( l.head  +  r ) :: ( l.tail  +  r )
      def  -  (l: H :: T, r: R): H :: T = ( l.head  -  r ) :: ( l.tail  -  r )
      def  *  (l: H :: T, r: R): H :: T = ( l.head  *  r ) :: ( l.tail  *  r )
      def  /  (l: H :: T, r: R): H :: T = ( l.head  /  r ) :: ( l.tail  /  r )
      def  %  (l: H :: T, r: R): H :: T = ( l.head  %  r ) :: ( l.tail  %  r )
      def min (l: H :: T, r: R): H :: T = ( l.head min r ) :: ( l.tail min r )
    }

  implicit class Ops[L](val l: L) extends AnyVal {
    def   + [R] (r: R)(implicit a: Arithmetic[L, R]): L = a.  +(l, r)
    def   - [R] (r: R)(implicit a: Arithmetic[L, R]): L = a.  -(l, r)
    def   * [R] (r: R)(implicit a: Arithmetic[L, R]): L = a.  *(l, r)
    def   / [R] (r: R)(implicit a: Arithmetic[L, R]): L = a.  /(l, r)
    def   % [R] (r: R)(implicit a: Arithmetic[L, R]): L = a.  %(l, r)
    def min [R] (r: R)(implicit a: Arithmetic[L, R]): L = a.min(l, r)
  }
}
