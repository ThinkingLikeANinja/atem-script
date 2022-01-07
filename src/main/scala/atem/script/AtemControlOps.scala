package atem.script

trait AtemControlOps:

  def sleep(frames: Int): AtemOp =
    AtemOp.SleepOp(frames)

  def userWait: AtemOp =
    AtemOp.UserWaitOp
