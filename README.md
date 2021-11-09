# ATEM Script

When playing with an *ATEM Mini* you will eventually discover the world of macros.

A macro is just an automation of a sequence of actions. Depending on your background, you might already expect to press a button to record all your individual actions and save it under some name afterwards, so in a single click you can repeat them.

But once you get more experience, or want to do something a bit more complex, you will end up editing the macros as some kind of text. In the *ATEM Mini* family, you can achieve that using *ATEM Software Control*, exporting your initial macro and opening it with some text editor.

Depending on your background, editing an XML file might feel ok, but especially if you come from some field where you are used to more powerful alternatives, like general purpose programing languages, considering that the same action can happen some dozen of times in a single macro, you would probably prefer to write `mic1Gain(2)` instead of `<Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-4"/>`. In other words, manually editing XML files is a repetitive error-prone source of pain.

Even if you have no prior programming experience, with the previous comparison you should already see how much easier would it be to create and maintain macros if they weren't stored in such a verbose format.

Going away from XML into something more like a script would bring all sorts of advantages, like the possibility to wrap complete sequences of actions under a meaningful name. 

For instance, if we frequently want to fade the mic1 volume to silence, we could write something like `mic1Gain(0), sleep(4), mic1Gain(-2), sleep(4), mic1Gain(-4), ...` and so on (which would already look better than the usual copy/paste of XML lines). But we could go one step further and have something like `mic1Fade()` wrapping the gain/sleep pair under some loop, so a single instruction would do exactly what we expect it to do.

That is what you can do with this project. Currently it is in a proof of concept state, so you would have to clone the repo to play with it and only a few operations are supported (`FairlightAudioMixerInputSourceFaderGain`, `MacroSleep` and `MacroUserWait`) specifically for the *ATEM Mini Pro*.

**How does it work?** Instead of editing XML files, you will only need to write a simple "AtemScript", that when executed will output the XML which you can import into *ATEM Software Control*.

**How does it look so far?** See the example below:

```scala
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
```

Running that script will output the following XML:

```xml
<?xml version='1.0' encoding='UTF-8'?>
<Profile majorVersion="1" minorVersion="5" product="ATEM Mini Pro">
    <MacroPool>
        <Macro index="0" name="mic1 fade macro" description="macro that takes the  gain to minus infinity">
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-1.17"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-1.38"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-1.61"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-1.89"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-2.22"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-2.61"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-3.06"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-3.59"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-4.21"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-4.94"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-5.79"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-6.8"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-7.97"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-9.35"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-10.97"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-12.87"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-15.1"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-17.72"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-20.79"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-24.38"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-28.61"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-33.56"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-39.37"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-46.19"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-54.19"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-63.57"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-74.58"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-87.49"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-102.64"/>
            <Op id="MacroSleep" frames="4"/>
            <Op id="FairlightAudioMixerInputSourceFaderGain" input="ExternalMic" sourceId="18446744073709486336" gain="-120.41"/>
        </Macro>
    </MacroPool>
    <MacroControl loop="False"/>
</Profile>
```

Stay tuned for new features and fell free to give your feedback, issues and wishes.
