package com.sample.action;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.ProcessState;

public class CriarServicosHandler implements ActionHandler {
	
	private static int cont;

	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		JbpmContext jbpmContext = null;
		try {
			jbpmContext = JbpmConfiguration.getInstance().createJbpmContext();
			
			List<String> subProcessos = new ArrayList<String>();
			subProcessos.add("process1");
			subProcessos.add("process2");
			
			ProcessDefinition pd = executionContext.getProcessInstance().getProcessDefinition();
			Node fork = (Node) executionContext.getProcessDefinition().getNode("servicos");
			
			Node join = (Node) executionContext.getProcessDefinition().getNode("uniao");
			
			fork.getLeavingTransitions().clear();
			join.getArrivingTransitions().clear();
			
			Node psZumbi = pd.getNode("psZumbi");
			if (psZumbi != null){
				System.out.println(pd.removeNode(psZumbi));
			}
			int cont = 0;
			long idProcesso = executionContext.getProcessInstance().getId();
			for(String subProcesso: subProcessos){
				cont++;
					//Cria o nó
					ProcessState processState = new ProcessState(idProcesso + " - Solicitação de Serviço - " + cont);
					ProcessDefinition subProcessDefinition = jbpmContext.getGraphSession().findLatestProcessDefinition(subProcesso);
					processState.setSubProcessDefinition(subProcessDefinition);

					//Liga o nó ao PD e vice-versa
					processState.setProcessDefinition(pd);
					pd.addNode(processState);
					
					
					//Cria as transições
					Transition forkParaProcessState = new Transition("fork para process-state " + cont);
					fork.addLeavingTransition(forkParaProcessState);
					processState.addArrivingTransition(forkParaProcessState);
					
					Transition processStateParaJoin = new Transition("process-state para join");
					processState.addLeavingTransition(processStateParaJoin);
					join.addArrivingTransition(processStateParaJoin);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally{
			if (jbpmContext != null){
				jbpmContext.close();
			}
		}
	}
}
