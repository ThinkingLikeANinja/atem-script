package atem.script

object Example extends AtemScript:

  given sourceId: String = "18446744073709486336"
  overwrite {

    atemMiniPro(
      "some macro" (
        "some description",
        userWait,
        mic1Gain(2),
        sleep(5),
        mic1Fade()
      ),
      "fade" (mic1Fade()),
      "linear fade" (mic2Fade(fadeFunction = linearFade))
    )

  }
