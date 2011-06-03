package exemplo.dominio.exceptions;

public class TokenExpiradoException extends Exception {

	private static final long serialVersionUID = 1L;

	public TokenExpiradoException(String message) {
		super(message);
	}

}
