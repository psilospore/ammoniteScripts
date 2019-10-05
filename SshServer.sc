import $ivy.`com.lihaoyi:ammonite-sshd_2.12.3:1.6.4`

import ammonite.sshd._
import org.apache.sshd.server.auth.pubkey.AcceptAllPublickeyAuthenticator

object AmmoniteServer {
  val hi = "hi"
  lazy val ammoniteServer = new SshdRepl(
    SshServerConfig(
      address = "localhost", // or "0.0.0.0" for public-facing shells
      port = 2222, // Any available port
      publicKeyAuthenticator = Some(AcceptAllPublickeyAuthenticator.INSTANCE), //TODO
    )
  )
  ammoniteServer.start()
}

