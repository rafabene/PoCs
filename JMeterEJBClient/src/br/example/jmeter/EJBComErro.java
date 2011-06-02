package br.example.jmeter;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import br.jus.trf1ejb.EJBExemplo;

public class EJBComErro extends AbstractJavaSamplerClient {

	@Override
	public SampleResult runTest(JavaSamplerContext jsCtx) {
		
		SampleResult resultado = new SampleResult();
		resultado.sampleStart();
		
		String servidor = jsCtx.getParameter("servidorEJB");
		
		Context ctx = getInitialContext(servidor);
		
		//Início do teste dos EJBS
		EJBExemplo ejbExemplo = null;
		try {
			ejbExemplo = (EJBExemplo) ctx.lookup("EJBExemploBeam/remote");
			ejbExemplo.metodoErro();
			resultado.setSuccessful(true);
			resultado.setResponseCodeOK();
			resultado.setResponseMessage("Resultado da invocação: Ok" );
		} catch (Exception e) {
			e.printStackTrace();
			resultado.setSuccessful(false);
		}
		
		resultado.sampleEnd();
		
		return resultado;
	}
	
	@Override
	public Arguments getDefaultParameters() {
		Arguments args = new Arguments();
		args.addArgument("servidorEJB", "localhost:1099");
		return args;
	}

	private Context getInitialContext(String servidor) {
		Hashtable<String, String> propiedades = new Hashtable<String, String>();
		propiedades.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
		propiedades.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
		propiedades.put(Context.PROVIDER_URL, "jnp://" + servidor);
		Context ctx = null;
		try {
			ctx = new InitialContext(propiedades);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return ctx;
	}

}
