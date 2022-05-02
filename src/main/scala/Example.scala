import atem.script.{given, *}

object Example extends AtemScript:

  given product: AtemProduct.AtemMiniPro = "18446744073709486336"
  overwrite {

    atemMacros(
      "mic1 fade macro" (
        "macro that takes the  gain to minus infinity",
        mic1Fade(),
        sleep(5)
      )
    )

  }
