package exemplo.dominio.servico;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import exemplo.dominio.exceptions.DescriptografiaException;
import exemplo.dominio.exceptions.TokenExpiradoException;
import exemplo.infraestrutura.CriptografiaService;

@Name("autenticadorService")
@AutoCreate
public class AutenticadorService  {

	private static final int MINUTOS = 5;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHm");

	@In
	private CriptografiaService criptografiaService;

	public String[] valoresToken(String chave, String token)
			throws DescriptografiaException {
		char[] valorDecifrado = criptografiaService.decode(chave, token);
		String valorDecrifradoAsString = new String(valorDecifrado);
		String assinatura = criptografiaService.SHA1(valorDecrifradoAsString);
		// Adiciona a assinatura do token na lista de valores
		String[] valores = (valorDecrifradoAsString + "|" + assinatura)
				.split("[|]");
		return valores;
	}

	public void verificaTimeStamp(String timestamp) throws ParseException,
			TokenExpiradoException {
		Date dataToken = sdf.parse(timestamp);
		Date dataAtual = new Date();
		long timeToken = dataToken.getTime();
		long timeAtual = dataAtual.getTime();
		long diferenca = MINUTOS * 1000 * 60;
		// Se não estiver no range de 5 minutos (CONSTANTE) para mais ou para menos
		if (!((timeToken + diferenca) > timeAtual && (timeToken - diferenca) < timeAtual)) {
			throw new TokenExpiradoException("Token expirado ou inválido");
		}
	}

}
