package atem.script

object Example extends AtemScript:

  given sourceId: String = "18446744073709486336"
  overwrite {

    atemMiniPro(
      "mic1 fade macro" (
        "macro that takes the  gain to minus infinity",
        mic1Fade()
      )
    )

  }
