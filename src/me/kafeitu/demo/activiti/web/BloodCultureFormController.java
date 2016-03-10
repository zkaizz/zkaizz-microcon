package me.kafeitu.demo.activiti.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.kafeitu.demo.activiti.entity.oa.Leave;
import me.kafeitu.demo.activiti.util.Page;
import me.kafeitu.demo.activiti.util.PageUtil;
import me.kafeitu.demo.activiti.util.UserUtil;
import me.kafeitu.modules.utils.ConstantUtil;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.form.StartFormDataImpl;
import org.activiti.engine.impl.form.TaskFormDataImpl;
import org.activiti.engine.impl.persistence.entity.SuspensionState;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.NativeTaskQuery;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value = "/bloodCulture")
public class BloodCultureFormController {
	public static String PROCESS_DEFINITION_KEY = "bloodCulture";
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private FormService formService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;

    //进入新建血标本的界面
    @RequestMapping(value = {"register", ""})
    public String createForm1(Model model) {
        return "/culture/bloodCulture";
    }
    
    /**
     * 模拟扫码
     */
    @RequestMapping(value = {"scancode", ""})
    public String scancode(Model model) {
        return "/culture/scancode";
    }
    
    /**
     * 处理条形码提交
     */
    @RequestMapping(value = "codehandle")
    @SuppressWarnings("unchecked")
    public String codehandle(@RequestParam(value = "scancode", required = true) String scancode,
    		Model model, HttpServletRequest request) {
        logger.debug("scancode is : {} ", scancode);
        
        String [] scancodeArray = scancode.split(ConstantUtil.SCAN_CODE_SPLIT);
        String pramaryCode = scancodeArray[0];
        String sql = "select distinct RES1.* from ACT_RU_TASK RES1 inner join EN_BLOODCULTURE B " +
        		"on B.PROCESS_INSTANCE_ID = RES1.PROC_INST_ID_ WHERE B.CODE = #{scancode} ";
        NativeTaskQuery query = taskService.createNativeTaskQuery().sql(sql)
                .parameter("scancode", pramaryCode);
        List<Task> tasks = query.list();
        if(scancodeArray.length > 1){
        	String subCode = scancodeArray[1];
        	for(Task task:tasks){
        		if(task.getId().equals(subCode)){
        			return "redirect:/bloodCulture/task/claim/"+subCode;
        		}
        	}
        	//如果所有的都没有匹配上，目前返回未查询到，后面可以改成展示所有主样本的子流程
        	model.addAttribute("message", "该条形码未查询到当前任务");
            return "/culture/scancode";
        }else{
	        if(tasks.size() == 1){
	        	return "redirect:/bloodCulture/task/claim/"+tasks.get(0).getId();
	        }else if(tasks.size()>1){
	        	model.addAttribute("tasks", tasks);
	        	return "/culture/bloodCulture-task-list";
	        }else{
	        	model.addAttribute("message", "该条形码未查询到当前任务");
	            return "/culture/scancode";
	        }
        }
    }
    
    
    /**
     * 创建血标本界面，获取数据渲染form
     */
    @RequestMapping(value = "get-form/start")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public Map<String, Object> findStartForm() throws Exception {
    	ProcessDefinitionQuery query1 = repositoryService.createProcessDefinitionQuery().processDefinitionKey(PROCESS_DEFINITION_KEY).active().orderByDeploymentId().desc();
        List<ProcessDefinition> list = query1.list();
        String processDefinitionId = list.get(0).getId();
    	Map<String, Object> result = new HashMap<String, Object>();
        StartFormDataImpl startFormData = (StartFormDataImpl) formService.getStartFormData(processDefinitionId);
        startFormData.setProcessDefinition(null);

    /*
     * 读取enum类型数据，用于下拉框
     */
        List<FormProperty> formProperties = startFormData.getFormProperties();
        for (FormProperty formProperty : formProperties) {
            Map<String, String> values = (Map<String, String>) formProperty.getType().getInformation("values");
            if (values != null) {
                for (Entry<String, String> enumEntry : values.entrySet()) {
                    logger.debug("enum, key: {}, value: {}", enumEntry.getKey(), enumEntry.getValue());
                }
                result.put("enum_" + formProperty.getId(), values);
            }
        }
        
        Map<String,String> defaultMap =  new HashMap<String,String>();
        defaultMap.put("code", "DSF0018272");
        defaultMap.put("sampleType", "血样本");
        defaultMap.put("patientName", "张三");
        defaultMap.put("patientGender", "男");
        defaultMap.put("patientAge", "23");
        defaultMap.put("patientCondition", "blablablabla");
        defaultMap.put("sampleRevTime", "2016-02-13 09:23:13");

        result.put("processDefinitionId", processDefinitionId);
        result.put("form", startFormData);
        result.put("defaultMap", defaultMap);
        return result;
    }
    
    /**
     *  创建血标本界面 提交操作
     */
    @RequestMapping(value = "start-process/{processDefinitionId}")
    @SuppressWarnings("unchecked")
    public String submitStartFormAndStartProcessInstance(@PathVariable("processDefinitionId") String processDefinitionId,
                                                         @RequestParam(value = "processType", required = false) String processType,
                                                         RedirectAttributes redirectAttributes,
                                                         HttpServletRequest request) {
        Map<String, String> formProperties = new HashMap<String, String>();

        // 从request中读取参数然后转换
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();
        for (Entry<String, String[]> entry : entrySet) {
            String key = entry.getKey();

            // fp_的意思是form paremeter
            if (StringUtils.defaultString(key).startsWith("fp_")) {
                formProperties.put(key.split("_")[1], entry.getValue()[0]);
            }
        }

        logger.debug("start form parameters: {}", formProperties);

        User user = UserUtil.getUserFromSession(request.getSession());
        // 用户未登录不能操作，实际应用使用权限框架实现，例如Spring Security、Shiro等
        if (user == null || StringUtils.isBlank(user.getId())) {
            return "redirect:/login?timeout=true";
        }
        ProcessInstance processInstance = null;
        try {
            identityService.setAuthenticatedUserId(user.getId());
            processInstance = formService.submitStartFormData(processDefinitionId, formProperties);
            logger.debug("start a processinstance: {}", processInstance);
        } finally {
            identityService.setAuthenticatedUserId(null);
        }
        redirectAttributes.addFlashAttribute("message", "任务已创建，请放入培养仪");

        return "redirect:/bloodCulture/task/list";
    }
    
    /**
     * 血标本列表查询
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "task/list")
    public ModelAndView taskList(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("/culture/bloodCulture-task-list");
        User user = UserUtil.getUserFromSession(request.getSession());

        List<Task> tasks = new ArrayList<Task>();

//        if (!StringUtils.equals(processType, "all")) {
//            /**
//             * 这里为了演示区分开自定义表单的请假流程，值读取leave-dynamic-from
//             * 在FormKeyController中有使用native方式查询的例子
//             */
//
//            List<Task> dynamicFormTasks = taskService.createTaskQuery().processDefinitionKey("leave-dynamic-from")
//                    .taskCandidateOrAssigned(user.getId()).active().orderByTaskId().desc().list();
//
//            List<Task> dispatchTasks = taskService.createTaskQuery().processDefinitionKey("dispatch")
//                    .taskCandidateOrAssigned(user.getId()).active().orderByTaskId().desc().list();
//
//            List<Task> leaveJpaTasks = taskService.createTaskQuery().processDefinitionKey("leave-jpa")
//                    .taskCandidateOrAssigned(user.getId()).active().orderByTaskId().desc().list();
//
//            tasks.addAll(dynamicFormTasks);
//            tasks.addAll(dispatchTasks);
//            tasks.addAll(leaveJpaTasks);
//        } else {
//            tasks = taskService.createTaskQuery().taskCandidateOrAssigned(user.getId()).active().orderByTaskId().desc().list();
//        }
        
        tasks = taskService.createTaskQuery().processDefinitionKey(PROCESS_DEFINITION_KEY)
              .taskCandidateOrAssigned(user.getId()).active().orderByTaskCreateTime().desc().list();
        mav.addObject("tasks", tasks);
        return mav;
    }
    
    /**
     * 点击处理时，授权给当前用户，并跳转到处理界面
     */
    @RequestMapping(value = "task/claim/{id}")
    public ModelAndView claim(@PathVariable("id") String taskId,HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("/culture/bloodCulture-peiyang");

        String userId = UserUtil.getUserFromSession(request.getSession()).getId();
        taskService.claim(taskId, userId);
        mav.addObject("taskId", taskId);
        return mav;
    }
    
    /**
     * 血标本当前处理的数据查询，用来渲染当前任务界面
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "get-form/task/{taskId}")
    @ResponseBody
    public Map<String, Object> findTaskForm(@PathVariable("taskId") String taskId) throws Exception {
        Map<String, Object> result = new HashMap<String, Object>();
        TaskFormDataImpl taskFormData = (TaskFormDataImpl) formService.getTaskFormData(taskId);
        String taskName = taskFormData.getTask().getName();
        // 设置task为null，否则输出json的时候会报错
        taskFormData.setTask(null);
        result.put("taskName", taskName);
        result.put("taskFormData", taskFormData);
    /*
     * 读取enum类型数据，用于下拉框
     */
        List<FormProperty> formProperties = taskFormData.getFormProperties();
        for (FormProperty formProperty : formProperties) {
            Map<String, String> values = (Map<String, String>) formProperty.getType().getInformation("values");
            if (values != null) {
                for (Entry<String, String> enumEntry : values.entrySet()) {
                    logger.debug("enum, key: {}, value: {}", enumEntry.getKey(), enumEntry.getValue());
                }
                result.put(formProperty.getId(), values);
            }
        }

        return result;
    }
    
    /**
     * 提交当前任务到下一步
     */
    @RequestMapping(value = "task/complete/{taskId}")
    @SuppressWarnings("unchecked")
    public String completeTask(@PathVariable("taskId") String taskId, @RequestParam(value = "processType", required = false) String processType,
                               RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Map<String, String> formProperties = new HashMap<String, String>();

        // 从request中读取参数然后转换
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<Entry<String, String[]>> entrySet = parameterMap.entrySet();
        for (Entry<String, String[]> entry : entrySet) {
            String key = entry.getKey();

            // fp_的意思是form paremeter
            if (StringUtils.defaultString(key).startsWith("fp_")) {
                formProperties.put(key.split("_")[1], entry.getValue()[0]);
            }
        }

        logger.debug("start form parameters: {}", formProperties);

        User user = UserUtil.getUserFromSession(request.getSession());

        // 用户未登录不能操作，实际应用使用权限框架实现，例如Spring Security、Shiro等
        if (user == null || StringUtils.isBlank(user.getId())) {
            return "redirect:/login?timeout=true";
        }
        try {
            identityService.setAuthenticatedUserId(user.getId());
            formService.submitTaskFormData(taskId, formProperties);
        } finally {
            identityService.setAuthenticatedUserId(null);
        }

        redirectAttributes.addFlashAttribute("message", "当前操作完成");
        return "redirect:/bloodCulture/task/list";
    }
    
    //***************************************************************************************************
    
    /**
     * 动态form流程列表
     *
     * @param model
     * @return
     */
    @RequestMapping(value = {"process-list", ""})
    public ModelAndView processDefinitionList(Model model, @RequestParam(value = "processType", required = false) String processType, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("/form/dynamic/dynamic-form-process-list", Collections.singletonMap("processType", processType));
        Page<ProcessDefinition> page = new Page<ProcessDefinition>(PageUtil.PAGE_SIZE);
        int[] pageParams = PageUtil.init(page, request);

        if (!StringUtils.equals(processType, "all")) {
            /*
             * 只读取动态表单的流程
             */
            ProcessDefinitionQuery query1 = repositoryService.createProcessDefinitionQuery().processDefinitionKey("leave-dynamic-from").active().orderByDeploymentId().desc();
            List<ProcessDefinition> list = query1.listPage(pageParams[0], pageParams[1]);

            ProcessDefinitionQuery query2 = repositoryService.createProcessDefinitionQuery().processDefinitionKey("dispatch").active().orderByDeploymentId().desc();
            List<ProcessDefinition> dispatchList = query2.listPage(pageParams[0], pageParams[1]);

            ProcessDefinitionQuery query3 = repositoryService.createProcessDefinitionQuery().processDefinitionKey("leave-jpa").active().orderByDeploymentId().desc();
            List<ProcessDefinition> list3 = query3.listPage(pageParams[0], pageParams[1]);

            list.addAll(list3);
            list.addAll(dispatchList);

            page.setResult(list);
            page.setTotalCount(query1.count() + query2.count());
        } else {
            // 读取所有流程
            ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery().active().orderByDeploymentId().desc();
            List<ProcessDefinition> list = query.list();
            page.setResult(list);
            page.setTotalCount(query.count());
        }

        mav.addObject("page", page);
        return mav;
    }

    /**
     * 运行中的流程实例
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "process-instance/running/list")
    public ModelAndView running(Model model, @RequestParam(value = "processType", required = false) String processType,
                                HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("/form/running-list", Collections.singletonMap("processType", processType));
        Page<ProcessInstance> page = new Page<ProcessInstance>(PageUtil.PAGE_SIZE);
        int[] pageParams = PageUtil.init(page, request);

        if (!StringUtils.equals(processType, "all")) {
            ProcessInstanceQuery leaveDynamicQuery = runtimeService.createProcessInstanceQuery()
                    .processDefinitionKey("leave-dynamic-from").orderByProcessInstanceId().desc().active();
            List<ProcessInstance> list = leaveDynamicQuery.listPage(pageParams[0], pageParams[1]);

            ProcessInstanceQuery dispatchQuery = runtimeService.createProcessInstanceQuery()
                    .processDefinitionKey("dispatch").active().orderByProcessInstanceId().desc();
            List<ProcessInstance> list2 = dispatchQuery.listPage(pageParams[0], pageParams[1]);
            list.addAll(list2);

            ProcessInstanceQuery leaveJpaQuery = runtimeService.createProcessInstanceQuery()
                    .processDefinitionKey("leave-jpa").active().orderByProcessInstanceId().desc();
            List<ProcessInstance> list3 = leaveJpaQuery.listPage(pageParams[0], pageParams[1]);
            list.addAll(list3);

            page.setResult(list);
            page.setTotalCount(leaveDynamicQuery.count() + dispatchQuery.count());
        } else {
            ProcessInstanceQuery dynamicQuery = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().desc().active();
            List<ProcessInstance> list = dynamicQuery.listPage(pageParams[0], pageParams[1]);
            page.setResult(list);
            page.setTotalCount(dynamicQuery.count());
        }
        mav.addObject("page", page);
        return mav;
    }

    /**
     * 已结束的流程实例
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "process-instance/finished/list")
    public ModelAndView finished(Model model, @RequestParam(value = "processType", required = false) String processType,
                                 HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("/form/finished-list", Collections.singletonMap("processType", processType));
        Page<HistoricProcessInstance> page = new Page<HistoricProcessInstance>(PageUtil.PAGE_SIZE);
        int[] pageParams = PageUtil.init(page, request);

        if (!StringUtils.equals(processType, "all")) {
            HistoricProcessInstanceQuery leaveDynamicQuery = historyService.createHistoricProcessInstanceQuery()
                    .processDefinitionKey("leave-dynamic-from").finished().orderByProcessInstanceEndTime().desc();
            List<HistoricProcessInstance> list = leaveDynamicQuery.listPage(pageParams[0], pageParams[1]);

            HistoricProcessInstanceQuery dispatchQuery = historyService.createHistoricProcessInstanceQuery()
                    .processDefinitionKey("dispatch").finished().orderByProcessInstanceEndTime().desc();
            List<HistoricProcessInstance> list2 = dispatchQuery.listPage(pageParams[0], pageParams[1]);

            HistoricProcessInstanceQuery leaveJpaQuery = historyService.createHistoricProcessInstanceQuery()
                    .processDefinitionKey("leave-jpa").finished().orderByProcessInstanceEndTime().desc();
            List<HistoricProcessInstance> list3 = leaveJpaQuery.listPage(pageParams[0], pageParams[1]);

            list.addAll(list2);
            list.addAll(list3);

            page.setResult(list);
            page.setTotalCount(leaveDynamicQuery.count() + dispatchQuery.count());
        } else {
            HistoricProcessInstanceQuery dynamicQuery = historyService.createHistoricProcessInstanceQuery()
                    .finished().orderByProcessInstanceEndTime().desc();
            List<HistoricProcessInstance> list = dynamicQuery.listPage(pageParams[0], pageParams[1]);
            page.setResult(list);
            page.setTotalCount(dynamicQuery.count());
        }

        mav.addObject("page", page);
        return mav;
    }

}
