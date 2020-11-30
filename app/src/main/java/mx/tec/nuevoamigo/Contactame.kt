package mx.tec.nuevoamigo

import android.R.id.message
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import mx.tec.nuevoamigo.utils.Credentials
import java.net.URLEncoder
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart


class Contactame : AppCompatActivity() {
    //MAIL SENDER
    lateinit var appExecutors: AppExecutors
    var Email = ""
    var nameUser: String = ""

    var telContacto = ""
    var corrContacto: String = ""
    var uid=""
    override fun onCreate(savedInstanceState: Bundle?) {
        //MAIL VARIABLE
        appExecutors = AppExecutors()
        //MAIL VARIABLE END

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contactame)
        val btnEmail = findViewById<Button>(R.id.btnCorreo)
        btnEmail.isEnabled = true

        var user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // Name, email address, and profile photo Url

            nameUser = user.displayName!!
            corrContacto = user.email!!
            uid=user.uid!!
        }
        val id = intent.getStringExtra("idPersona")
        val nombrePerrito = intent.getStringExtra("nombrePerrito")
        val idPerro = intent.getStringExtra("idPerro")

        val nombre = findViewById<TextView>(R.id.txtNombreC)
        val horarioAtencion = findViewById<TextView>(R.id.tVHorario)
        val tipo = findViewById<TextView>(R.id.tVTipo)
        val ciudad = findViewById<TextView>(R.id.txtCiudadC)
        var telefono = ""
        var personita = ""


        val db = FirebaseFirestore.getInstance()

        db.collection("Persona").document(id!!)
            .get()
            .addOnSuccessListener { document ->
                if (document !== null) {
                    personita = document["Nombre"].toString()
                    nombre?.text = document["Nombre"].toString()
                    horarioAtencion.text = document["HorarioAtencion"].toString()
                    if (document["IsAlbergue"].toString() == "false")
                        tipo.text = "Persona"
                    else
                        tipo.text = "Albergue"
                    ciudad.text = document["Ciudad"].toString()
                    Email = document["Email"].toString()
                    telefono = document["Telefono"].toString()
                } else {
                    Toast.makeText(
                        this,
                        "Hubo un error al recuperar el usuario, intentelo más tarde",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        db.collection("Persona").document(uid).get().addOnSuccessListener { document ->
            if (document !== null) {
                telContacto = document["Telefono"].toString()
                //corrContacto = document["Email"].toString()
            } else {
                Toast.makeText(
                    this,
                    "Hubo un error al recuperar el usuario, intentelo más tarde",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val btnWhats = findViewById<Button>(R.id.btnWha)
            btnWhats.setOnClickListener {

                val mensaje =
                    "Hola ${nombre?.text.toString()}, soy $nameUser, te contacté por medio de la aplicación Nuevo Amigo. Estoy interesado/a en adoptar a tu perrito/a ${nombrePerrito.toString()}."
                val url =
                    "https://api.whatsapp.com/send?phone=+52 $telefono" + "&text=" + URLEncoder.encode(
                        mensaje,
                        "UTF-8"
                    )

                val intent = Intent()
                intent.type = "text/plain"


                intent.setPackage("com.whatsapp")
                intent.data = Uri.parse(url)


                try {
                    startActivity(intent)
                } catch (ex: ActivityNotFoundException) {
                    var builder = AlertDialog.Builder(this)
                    builder.setTitle("Whatsapp no instalado")
                    builder.setMessage("Asegúrate de tener instalada la aplicación de whatsapp, para ejecutar está función.")
                    builder.setPositiveButton("ENTENDIDO",
                        { dialogInterface: DialogInterface, i: Int -> })
                    builder.show()
                }

                //el telefono ya esta en la variable telefono
            }


            //SHARED PREFERENCE
            btnEmail.setOnClickListener {
                val sp = getSharedPreferences(idPerro, Context.MODE_PRIVATE)
                if (sp.getLong("pasado", 0) == 0.toLong()) {
                    val currentTime = Date().time
                    with(sp.edit()) {
                        putLong("pasado", currentTime)
                        commit()
                    }
                    sendEmail(personita, nombrePerrito.toString())
                } else {
                    val previousTime = sp.getLong("pasado", 0);
                    if (Date().time - previousTime > 86400000) {
                        //86400000
                        btnEmail.isEnabled = true
                        with(sp.edit()) {
                            remove("pasado")
                            commit()
                        }
                    } else {
                        btnEmail.isEnabled = false
                        Toast.makeText(
                            this,
                            "No puedes enviar muchos correos al mismo tiempo, inténtalo más tarde",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }

    }

    //EMAIL
    private fun sendEmail(nombProp: String, nombPerr: String) {
        val cred = Credentials()
        appExecutors.diskIO().execute {
            val props = System.getProperties()
            props.put("mail.smtp.host", "smtp.gmail.com")
            props.put("mail.smtp.socketFactory.port", "465")
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "465")

            val session = Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(cred.correo, cred.contra)
                    }
                })

            try {
                //Creating MimeMessage object
                val mm = MimeMessage(session)
                //Setting sender address
                mm.setFrom(InternetAddress(cred.correo))
                //cred.correo
                //Adding receiver
                mm.addRecipient(
                    Message.RecipientType.TO,
                    InternetAddress(Email)
                )

                val mensajito =
                    "Somos del equipo de Nuevo Amigo.\n " +
                            "Parece que alguien está interesado en uno de sus perritos puestos en adopción, llamado $nombPerr. \n " +
                            "Responda al solicitante lo antes posible, para que lleguen a un acuerdo sobre la adopción de $nombPerr. \n " +
                            "Siempre estamos al tanto de nuestros usuarios, y de nuestros amigos perrunos. \n " +
                            "Datos del adoptante: \n" +
                            "Nombre del solicitante: $nameUser \n" +
                            "Teléfono: $telContacto \n" +
                            "Correo: $corrContacto \n"


                //Adding subject
                mm.subject = "Adoptar perrito"
                mm.setContent(
                    "<div dir=\"ltr\">\n" +
                            "  <center>\n" +
                            "    <table id=\"bodyTable\" style=\"border-collapse: collapse; height: 100%; margin: 0; padding: 0; width: 100%; background-color: #e8ecec;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" align=\"center\">\n" +
                            "      <tbody>\n" +
                            "        <tr>\n" +
                            "          <td id=\"bodyCell\" style=\"height: 100%; margin: 0; padding: 10px; width: 100%; border-top: 0;\" align=\"center\" valign=\"top\">\n" +
                            "            <table class=\"templateContainer\" style=\"border-collapse: collapse; border: 0; max-width: 600px!important;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                            "              <tbody>\n" +
                            "                <tr>\n" +
                            "                  <td id=\"templatePreheader\" style=\"background: #fafafa url('https://images.unsplash.com/photo-1601758064224-c3c5ec910095?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1147&q=80') no-repeat top/contain; background-color: #fafafa; background-image: url('https://images.unsplash.com/photo-1601758064224-c3c5ec910095?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1147&q=80'); background-repeat: no-repeat; background-position: top; background-size: contain; border-top: 0; border-bottom: 0; padding-top: 380px; padding-bottom: 0px;\" valign=\"top\">\n" +
                            "                    <table class=\"mcnTextBlock\" style=\"min-width: 100%; border-collapse: collapse;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                            "                      <tbody class=\"mcnTextBlockOuter\">\n" +
                            "                        <tr>\n" +
                            "                          <td class=\"mcnTextBlockInner\" style=\"padding-top: 9px;\" valign=\"top\">\n" +
                            "                            <table class=\"mcnTextContentContainer\" style=\"max-width: 100%; min-width: 100%; border-collapse: collapse;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">\n" +
                            "                              <tbody>\n" +
                            "                                <tr>\n" +
                            "                                  <td class=\"mcnTextContent\" style=\"padding: 0px 18px 9px; line-height: 150%; word-break: break-word; text-align: left;\" valign=\"top\">\n" +
                            "                                    <div style=\"text-align: center;\">\n" +
                            "                                      <br>\n" +
                            "                                      <br>\n" +
                            "                                      <span style=\"color: #5ea6a9; font-family: georgia, times, times new roman, serif;\">\n" +
                            "                                        <span style=\"font-size: 40px;\">\n" +
                            "                                          <strong>\n" +
                            "                                            <em>Buen día $nombProp</em>\n" +
                            "                                          </strong>\n" +
                            "                                        </span>\n" +
                            "                                      </span> \n" +
                            "                                      <br>\n" +
                            "                                      <span style=\"color: #656565; font-family: Helvetica;\">\n" +
                            "                                        <span style=\"font-size: 12px;\">&nbsp;</span>\n" +
                            "                                      </span>\n" +
                            "                                    </div>\n" +
                            "                                  </td>\n" +
                            "                                </tr>\n" +
                            "                              </tbody>\n" +
                            "                            </table>\n" +
                            "                          </td>\n" +
                            "                        </tr>\n" +
                            "                      </tbody>\n" +
                            "                    </table>\n" +
                            "                  </td>\n" +
                            "                </tr>\n" +
                            "                <tr>\n" +
                            "                  <td id=\"templateHeader\" style=\"background: #ffffff none no-repeat center/cover; background-color: #ffffff; background-image: none; background-repeat: no-repeat; background-position: center; background-size: cover; border-top: 0; border-bottom: 0; padding-top: 9px; padding-bottom: 0;\" valign=\"top\">\n" +
                            "                    <table class=\"mcnDividerBlock\" style=\"min-width: 100%; border-collapse: collapse; table-layout: fixed!important;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                            "                      <tbody class=\"mcnDividerBlockOuter\">\n" +
                            "                        <tr>\n" +
                            "                          <td class=\"mcnDividerBlockInner\" style=\"min-width: 100%; padding: 0px 18px;\">\n" +
                            "                            <table class=\"mcnDividerContent\" style=\"min-width: 100%; border-top: 2px none #eaeaea; border-collapse: collapse;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                            "                              <tbody>\n" +
                            "                                <tr>\n" +
                            "                                  <td style=\"mso-line-height-rule: exactly; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;\">&nbsp;</td>\n" +
                            "                                </tr>\n" +
                            "                              </tbody>\n" +
                            "                            </table>\n" +
                            "                          </td>\n" +
                            "                        </tr>\n" +
                            "                      </tbody>\n" +
                            "                    </table>\n" +
                            "                  </td>\n" +
                            "                </tr>\n" +
                            "                <tr>\n" +
                            "                  <td id=\"templateBody\" style=\"background: #fafafa none no-repeat center/cover; background-color: #fafafa; background-image: none; background-repeat: no-repeat; background-position: center; background-size: cover; border-top: 0; border-bottom: 2px none #eaeaea; padding-top: 0; padding-bottom: 9px;\" valign=\"top\">\n" +
                            "                    <table class=\"mcnTextBlock\" style=\"min-width: 100%; border-collapse: collapse;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                            "                      <tbody class=\"mcnTextBlockOuter\">\n" +
                            "                        <tr>\n" +
                            "                          <td class=\"mcnTextBlockInner\" style=\"padding-top: 9px;\" valign=\"top\">\n" +
                            "                            <table class=\"mcnTextContentContainer\" style=\"max-width: 100%; min-width: 100%; border-collapse: collapse;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" align=\"left\">\n" +
                            "                              <tbody>\n" +
                            "                                <tr>\n" +
                            "                                  <td class=\"mcnTextContent\" style=\"padding: 0px 18px 9px; line-height: 200%; word-break: break-word; color: #202020; font-family: Helvetica; font-size: 16px; text-align: left;\" valign=\"top\">\n" +
                            "                                    <div style=\"text-align: center;\">\n" +
                            "                                      <br>\n" +
                            "                                      <span style=\"color: #808080;\">\n" +
                            "                                        <span style=\"font-family: merriweather,georgia,times new roman,serif;\">\n" +
                            "                                          <span style=\"font-size: 18px;\"> $mensajito\n" +
                            "                                            <br>\n" +
                            "                                            <br>\n" +
                            "                                            <br>De parte de: \n" +
                            "                                            <br>El equipo de Nuevo Amigo\n" +
                            "                                          </span>\n" +
                            "                                        </span>\n" +
                            "                                      </span>\n" +
                            "                                    </div>\n" +
                            "                                  </td>\n" +
                            "                                </tr>\n" +
                            "                              </tbody>\n" +
                            "                            </table>\n" +
                            "                          </td>\n" +
                            "                        </tr>\n" +
                            "                      </tbody>\n" +
                            "                    </table>\n" +
                            "                  </td>\n" +
                            "                </tr>\n" +
                            "                <tr>\n" +
                            "                  <td id=\"templateFooter\" style=\"background: #fafafa none no-repeat center/cover; background-color: #fafafa; background-image: none; background-repeat: no-repeat; background-position: center; background-size: cover; border-top: 0; border-bottom: 0; padding-top: 9px; padding-bottom: 9px;\" valign=\"top\">\n" +
                            "                    <table class=\"mcnDividerBlock\" style=\"min-width: 100%; border-collapse: collapse; table-layout: fixed!important;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                            "                      <tbody class=\"mcnDividerBlockOuter\">\n" +
                            "                        <tr>\n" +
                            "                          <td class=\"mcnDividerBlockInner\" style=\"min-width: 100%; padding: 0px 18px;\">\n" +
                            "                            <table class=\"mcnDividerContent\" style=\"min-width: 100%; border-top: 2px none #eaeaea; border-collapse: collapse;\" border=\"0\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                            "                              <tbody>\n" +
                            "                                <tr>\n" +
                            "                                  <td style=\"mso-line-height-rule: exactly; -ms-text-size-adjust: 100%; -webkit-text-size-adjust: 100%;\">&nbsp;</td>\n" +
                            "                                </tr>\n" +
                            "                              </tbody>\n" +
                            "                            </table>\n" +
                            "                          </td>\n" +
                            "                        </tr>\n" +
                            "                      </tbody>\n" +
                            "                    </table>\n" +
                            "                  </td>\n" +
                            "                </tr>\n" +
                            "              </tbody>\n" +
                            "            </table>\n" +
                            "          </td>\n" +
                            "        </tr>\n" +
                            "      </tbody>\n" +
                            "    </table>\n" +
                            "  </center>\n" +
                            "  <span id=\"r_293824_chq_template_marker_in_email_span\" style=\"line-height: 0px; height: 0px; width: 0px;\"></span>\n" +
                            "</div>", "text/html"
                )

                //Sending email
                Transport.send(mm)

                appExecutors.mainThread().execute {
                    //Something that should be executed on main thread.
                    Toast.makeText(this, "Correo enviado", Toast.LENGTH_SHORT).show()
                }

            } catch (e: MessagingException) {
                e.printStackTrace()
            }
        }
    }
}