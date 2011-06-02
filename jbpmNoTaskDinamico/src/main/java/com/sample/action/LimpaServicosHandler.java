package com.sample.action;

import java.util.Iterator;
import java.util.List;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;

public class LimpaServicosHandler implements ActionHandler {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		JbpmContext jbpmContext = null;
		try {
			jbpmContext = JbpmConfiguration.getInstance().createJbpmContext();
			long idProcesso = executionContext.getProcessInstance().getId();
			ProcessDefinition pd = executionContext.getProcessInstance().getProcessDefinition();
			List<Node> nosExistentes = pd.getNodes();
			Iterator<Node> it = nosExistentes.iterator();
			while (it.hasNext()) {
				Node no = it.next();
				String nomeNo = no.getName();
				if (nomeNo.contains("Solicitação de Serviço") && nomeNo.startsWith(String.valueOf(idProcesso))){
					it.remove();
				}
			}
		}finally{
			if (jbpmContext != null){
				jbpmContext.close();
			}
		}
	}

}
