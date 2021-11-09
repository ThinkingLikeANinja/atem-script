package atem.script

import java.io.*
import java.text.{DecimalFormat, DecimalFormatSymbols}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import scala.xml.*

trait AtemScriptWriter:

  private val pp = new PrettyPrinter(200, 4)

  private val symbols = new DecimalFormatSymbols(Locale.ROOT)
  private val df = new DecimalFormat("#.##;-#.##", symbols)

  private def opToXml(op: AtemOp) =
    op match
      case AtemOp.SleepOp(frames) => <Op id="MacroSleep" frames={s"$frames"}/>
      case AtemOp.UserWaitOp      => <Op id="MacroUserWait"/>
      case AtemOp.AudioInputGainOp(input, gain, sourceId) =>
        <Op id="FairlightAudioMixerInputSourceFaderGain" input={input.name} sourceId={sourceId} gain={
          df.format(gain)
        }/>

  private def profileToXml(profile: AtemProfile): String =
    pp.format(
      <Profile majorVersion="1" minorVersion="5" product={profile.product.name}>
        <MacroPool>
          {
        for
          i <- profile.macros.indices
          aMacro = profile.macros(i)
        yield {
          <Macro index={i.toString} name={aMacro.name} description={
            aMacro.description.getOrElse("")
          }>
              {
            for
              op <- aMacro.ops
              xml = opToXml(op)
            yield xml
          }
            </Macro>
        }
      }
        </MacroPool>
        <MacroControl loop="False"/>
      </Profile>
    )

  private given Conversion[AtemProfile, String] with
    def apply(profile: AtemProfile): String = profileToXml(profile)

  private def write(fileName: String, profile: AtemProfile): Unit =
    val outputDirName = "out"
    val od = File(outputDirName)
    if !od.exists() then od.mkdir()
    XML.save(
      s"$outputDirName/$fileName.xml",
      XML.loadString(profile),
      "UTF-8",
      xmlDecl = true,
      doctype = null
    )

  def overwrite(profile: AtemProfile): Unit =
    write(this.getClass.getSimpleName, profile)

  private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")

  def timestampedWrite(profile: AtemProfile): Unit =
    val timeSuffix = LocalDateTime.now().format(formatter)
    write(s"${this.getClass.getSimpleName} $timeSuffix", profile)

  def printXml(profile: AtemProfile): Unit = println(profileToXml(profile))
