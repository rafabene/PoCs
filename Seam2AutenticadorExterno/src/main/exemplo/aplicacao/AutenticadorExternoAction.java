package exemplo.aplicacao;

import java.text.ParseException;
import java.util.Map;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import exemplo.dominio.exceptions.DescriptografiaException;
import exemplo.dominio.exceptions.TokenExpiradoException;
import exemplo.dominio.servico.AutenticadorService;

@Name("autenticadorExternoAction")
public class AutenticadorExternoAction {
	
	private static final String SAL = "SAL"; //TODO Definir o sal que será usado
	
	@Logger
	private Log log;

	@In
	private Credentials credentials;

	@In
	private Identity identity;

	@In
	private StatusMessages statusMessages;
	
	@In
	private AutenticadorService autenticadorService;
	
	
	public void autenticarUsuarioExterno() {
		// Limpa a mensagem de Please, Login First!
		statusMessages.clear();

		Map<String, Object> parametros = Redirect.instance().getParameters();
		String user = (String) parametros.get("user");
		String token = (String) parametros.get("token");
		String hash = (String) parametros.get("hash");
		if (user != null && token != null && hash != null){
			try {

				String[] valores = autenticadorService.valoresToken(SAL + user, token);
				//Caso a assinatura informada não corresponda a assinatura do token
				if (! hash.equals(valores[3])){
					throw new DescriptografiaException("Assinatura não confere", null);
				}
				
				//Descarta os parâmetros da URL - Deve ser feito antes de chamar o método identity.login()
				parametros.remove("user");
				parametros.remove("token");
				parametros.remove("hash");
				
				//Verifica se o token ainda é valido
				String timestamp = valores[1];
				autenticadorService.verificaTimeStamp(timestamp);
				
				credentials.setUsername(user);
				credentials.setPassword(valores[2]);
				identity.login();
			} catch (ParseException e) {
				statusMessages.add(Severity.ERROR, "Erro: " + e.getMessage());
				e.printStackTrace();
			} catch (TokenExpiradoException e) {
				statusMessages.add(Severity.ERROR, e.getMessage());
			} catch (DescriptografiaException e) {
				//Pode acontecer quando houve tentativa de fraude - Usuário não é informado, apenas log.
				log.error("Possível tentativa de fraude do Token: " + e.getMessage() );
			}
		}
	}


}
