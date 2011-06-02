package com.sample.action;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;

public class CriaTasks implements ActionHandler {

	private static final long serialVersionUID = 1L;

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		Task t1 = new Task("teste");
		t1.setProcessDefinition(executionContext.getProcessDefinition());
		t1.setDescription("Criado dinâmicamente");
		
		TaskNode taskNode = (TaskNode) executionContext.getNode();
		executionContext.getProcessInstance().getTaskMgmtInstance().getTaskMgmtDefinition().addTask(t1);
		taskNode.addTask(t1);

	}

}
