package org.lasersonlab.zarr

import cats.implicits._
import cats.{ Id, Traverse }
import hammerlab.shapeless.tlist._
import org.lasersonlab.ndarray.Ints._
import org.lasersonlab.ndarray.Vectors
import org.lasersonlab.ndarray.Vectors._
import shapeless.Lazy

/**
 * Iterate over an N-dimensional range of integers (provided as a `Shape`), stored as an [[A]]
 */
trait Indices[A[_]] {
  type Shape
  def apply(shape: Shape): A[Shape]
}
object Indices {

  type Aux[A[_], _Shape] = Indices[A] { type Shape = _Shape }

  implicit val tnil: Aux[Id, TNil] =
    new Indices[Id] {
      type Shape = TNil
      def apply(tnil: TNil): Id[TNil] = tnil
    }

  // NOTE: the compiler doesn't use this to derive instances for `Vectors` types
  implicit def cons[TL <: TList, Row[_]](
    implicit
    e: Lazy[Aux[Row, TL]],
    f: Traverse[Row],
    pp: Prepend[Int, TL]
  ):
  Aux[
    Vectors.Aux[?, Row],
    Int :: TL
  ] =
    new Indices[Vectors.Aux[?, Row]] {
      type Shape = Int :: TL
      def apply(shape: Shape): Vectors.Aux[Shape, Row] =
        shape match {
          case h :: t ⇒
            Vectors.make[Shape, Row](
              (0 until h)
                .map {
                  i ⇒
                    f.map(
                      e.value(t)
                    ) {
                      i :: _
                    }
                }
                .toVector
            )
        }
    }

  // Vector CanBuildFrom
  import hammerlab.collection._

  // The auto-derivations needed below fail, seemingly due to unification issues / general inability to work with HKTs,
  // so here are some manual instances:

  implicit val     indices1:      Indices.Aux[Vector1, Ints1]  =
  new Indices[Vector1] {
    type Shape = Ints1
    def apply(shape: Shape): Vector1[Shape] =
      shape match {
        case n :: TNil ⇒
          (0 until n).map[Shape, Vector[Shape]]{ _ :: TNil }
      }
  }

  // This crashes the compiler if it is searched for via `cons` above
  implicit val lazyIndices1: Lazy[Indices.Aux[Vector1, Ints1]] = Lazy(indices1)

  implicit val     indices2:      Indices.Aux[Vector2, Ints2]  = Indices.cons[Ints1, Vector1]
  implicit val lazyIndices2: Lazy[Indices.Aux[Vector2, Ints2]] = Lazy(indices2)

  implicit val     indices3:      Indices.Aux[Vector3, Ints3]  = Indices.cons[Ints2, Vector2]
  implicit val lazyIndices3: Lazy[Indices.Aux[Vector3, Ints3]] = Lazy(indices3)

  implicit val     indices4:      Indices.Aux[Vector4, Ints4]  = Indices.cons[Ints3, Vector3]
  implicit val lazyIndices4: Lazy[Indices.Aux[Vector4, Ints4]] = Lazy(indices4)

  implicit val     indices5:      Indices.Aux[Vector5, Ints5]  = Indices.cons[Ints4, Vector4]
  implicit val lazyIndices5: Lazy[Indices.Aux[Vector5, Ints5]] = Lazy(indices5)

  implicit val     indices6:      Indices.Aux[Vector6, Ints6]  = Indices.cons[Ints5, Vector5]
  implicit val lazyIndices6: Lazy[Indices.Aux[Vector6, Ints6]] = Lazy(indices6)
}
