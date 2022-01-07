package atem.script

import scala.math.*

given Conversion[AtemOp, AtemMacroSection] with
  def apply(op: AtemOp): AtemMacroSection = AtemMacroSection(List(op))

given Conversion[List[AtemOp], AtemMacroSection] with
  def apply(ops: List[AtemOp]): AtemMacroSection = AtemMacroSection(ops)

given Conversion[AtemMacroSection, List[AtemOp]] with
  def apply(macroSection: AtemMacroSection): List[AtemOp] = macroSection.ops

given Conversion[String, AtemProduct.AtemMiniPro] with
  def apply(sourceId: String): AtemProduct.AtemMiniPro = AtemProduct.AtemMiniPro(sourceId)

trait AtemScript extends App with AtemMicOps with AtemScriptWriter:

  def atemMacros(macros: AtemMacro*)(using product: AtemProduct.AtemMiniPro): AtemProfile =
    AtemProfile(product, macros.toList)

  extension (sections: Seq[AtemMacroSection])
    def combineSections =
      sections.toList.reduce(_.combine(_))

  extension (name: String)

    def apply(op: AtemOp, ops: AtemOp*) =
      AtemMacro(name, op :: ops.toList)

    def apply(section: AtemMacroSection, sections: AtemMacroSection*) =
      AtemMacro(name, (section :: (if sections.isEmpty then Nil else sections.toList)).combineSections)

    def apply(description: String, sections: AtemMacroSection*) =
      AtemMacro(name, sections.combineSections, Some(description))
