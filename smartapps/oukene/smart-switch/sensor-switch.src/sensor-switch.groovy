/**
 *  Sensor Switch (v.0.0.1)
 *
 *  Authors
 *   - oukene
 *  Copyright 2021
 *
 */

import groovy.transform.Field
 
definition(
    name: "Sensor Switch",
    namespace: "oukene/smart-switch",
    author: "oukene",
    description: "센서로 스위치 작동",
    category: "My Apps",
    pausable: true,
    
  	parent: "oukene/smart-switch/parent:Smart Switch",
    iconUrl: "https://cdn4.iconfinder.com/data/icons/basic-ui-element-2-3-filled-outline/512/Basic_UI_Elements_-_2.3_-_Filled_Outline_-_44-29-512.png",
    iconX2Url: "https://cdn4.iconfinder.com/data/icons/basic-ui-element-2-3-filled-outline/512/Basic_UI_Elements_-_2.3_-_Filled_Outline_-_44-29-512.png",
    iconX3Url: "https://cdn4.iconfinder.com/data/icons/basic-ui-element-2-3-filled-outline/512/Basic_UI_Elements_-_2.3_-_Filled_Outline_-_44-29-512.png"
)

preferences
{
	page(name: "dashBoardPage", install: false, uninstall: true)
	page(name: "sensorPage", install: false, uninstall: true, nextPage: "switchPage")
    page(name: "switchPage", install: false, uninstall: true, nextPage: "optionPage")
	page(name: "optionPage", install: true, uninstall: true)
}

def dashBoardPage(){
	dynamicPage(name: "dashBoardPage", title:"[Dash Board]", refreshInterval:1) {
    	try
        {
            if(state.initialize)
            {
                section() {
                    paragraph "- DashBoard", image: "https://cdn4.iconfinder.com/data/icons/finance-427/134/23-512.png"
                    paragraph "[ 스위치 켜짐 조건 ]"
                    
                    if(contactSensor != null) {
                    	def isList = contactSensor instanceof List
                        if(isList) {
                            contactSensor.each {
                                paragraph "" + it.displayName + " - " + (it.currentState("contact").value == contactSensorAction ? "true" : "false")
                            }
                        }
                        else { paragraph "" + contactSensor.displayName + " - " + (contactSensor.currentState("contact").value == contactSensorAction ? "true" : "false")	}
                    }
                    if(motionSensor != null) {
                    	def isList = motionSensor instanceof List
                        if(isList) {
                            motionSensor.each {
                                paragraph "" + it.displayName + " - " + (it.currentState("motion").value == motionSensorAction ? "true" : "false")
                            }
                        }
                        else { paragraph "" + motionSensor.displayName + " - " + (motionSensor.currentState("motion").value == motionSensorAction ? "true" : "false")	}
                    }
                    if(switches != null) {
                    	def isList = switches instanceof List
                        if(isList) {
                            switches.each {
                                paragraph "" + it.displayName + " - " + (it.currentState("switch").value == switchAction ? "true" : "false")
                            }
                        }
                        else { paragraph "" + switches.displayName + " - " + (switches.currentState("motion").value == switchAction ? "true" : "false")	}
                    }
                    if(waterSensor != null) {
                    	def isList = waterSensor instanceof List
                        if(isList) {
                            waterSensor.each {
                                paragraph "" + it.displayName + " - " + (it.currentState("water").value == waterSensorAction ? "true" : "false")
                            }
                        }
                        else { paragraph "" + waterSensor.displayName + " - " + (waterSensor.currentState("motion").value == waterSensorAction ? "true" : "false")	}
                    }
                    
                    paragraph "스위치 켜진시각: " + new Date(state.on_time).format('yyyy-MM-dd HH:mm:ss.SSS', location.getTimeZone())
                    paragraph "스위치 꺼진시각: " + new Date(state.off_time).format('yyyy-MM-dd HH:mm:ss.SSS', location.getTimeZone())
                    paragraph "수동모드: " + (useManualMode == true ? "사용" : "미사용")
                    if(light_meter) 
                    { 
                        paragraph "현재 조도: " + light_meter.currentIlluminance + ", 기준 조도: " + lux_max 
                    }
                    else { paragraph "조도 센서 미사용" }
                    if(true == useManualMode)
                    {
                        paragraph "모드: " + (state.autoMode == true ? "자동모드" : "수동모드")
                    }
                }
            }          
            section() {
                href "sensorPage", title: "설정", description:"", image: "https://cdn4.iconfinder.com/data/icons/industrial-1-4/48/33-512.png"
            }
            if(state.initialize)
            {
                section()
                {
                    href "optionPage", title: "옵션", description:"", image: "https://cdn4.iconfinder.com/data/icons/multimedia-internet-web/512/Multimedia_Internet_Web-16-512.png"
                }
            }
		}
        catch(all)
        {
        	section("설정이 올바르지 않습니다. 재설정해주세요") {
                href "sensorPage", title: "설정", description:"", image: "https://cdn4.iconfinder.com/data/icons/industrial-1-4/48/33-512.png"
            }
        }
    }
}


def sensorPage()
{
	dynamicPage(name: "sensorPage", title: "설정", nextPage: "switchPage")
    {
    	log.debug location.getTimeZone()
        section()
        {
        	input(name: "contactSensor", type: "capability.contactSensor", title: "열림감지 센서", required: false, multiple: true)
            input(name: "contactSensorAction", type: "enum", title: "다음 동작이 발생하면", options: attributeValues("contactSensor"), required: true, defaultValue: "open")
            input(name: "motionSensor", type: "capability.motionSensor", title: "모션감지 센서", required: false, multiple: true)
            input(name: "motionSensorAction", type: "enum", title: "다음 동작이 발생하면", options: attributeValues("motionSensor"), required: true, defaultValue: "active")
            input(name: "switches", type: "capability.switch", title: "스위치", required: false, multiple: true)
            input(name: "switchAction", type: "enum", title: "다음 동작이 발생하면", options: attributeValues("switch"), required: true, defaultValue: "on")
            input(name: "waterSensor", type: "capability.waterSensor", title: "물 감지 센서", required: false, multiple: true)
            input(name: "waterSensorAction", type: "enum", title: "다음 동작이 발생하면", options: attributeValues("waterSensor"), required: true, defaultValue: "wet")
            
            /*
            input "sensorType", "enum", title: "센서 선택", multiple: false, required: true, submitOnChange: true, options: [
                "contactSensor":"열림감지 센서",
                "motionSensor":"움직임 감지 센서",
                "switch": "스위치",
                "button": "버튼",
                "waterSensor": "물 감지 센서"]
			
            if(sensorType != null && sensorType != "null") {
                input(name: "sensor", type: "capability.$sensorType", title: "$sensorType 에서", required: true, multiple: false, submitOnChange: true)
                input(name: "sensorAction", type: "enum", title: "다음 동작이 발생하면", options: attributeValues(sensorType), required: true)
            }
            */
        }
        section() {
        	input "isAllDevices", "bool", title: "선택된 디바이스 모두에서 조건 만족 할 경우에만 켜짐", required: true, defaultValue: false
        }
    }
}

def switchPage()
{    
    dynamicPage(name: "switchPage", title: "", nextPage: "optionPage")
    {
    	if(null == contactSensor && null == motionSensor &&  null == switchAction && null == waterSensor)
        {
        	section("조건으로 작동할 디바이스가 선택되지 않았습니다") {
                href "sensorPage", title: "설정", description:"", image: "https://cdn4.iconfinder.com/data/icons/industrial-1-4/48/33-512.png"
            }
		}
        else
        {
        	log.debug "is All Device: " + isAllDevices
            section("스위치 설정") {
                input(name: "main_switch", type: "capability.switch", title: "이 스위치와", required: true)
                input(name: "sub_switch", type: "capability.switch", title: "추가로 이 스위치들을 켭니다", multiple: true, required: false)
                if(main_switch)
                {
                    input "isAllDevices_off", "bool", title: "선택된 디바이스 모두에서 조건이 만족되지 않을 경우에만 꺼짐", required: true, defaultValue: true
                }
            }
        }
    }
}

def timeInputs() {
    section {
        input "startTime", "time", title: "자동화 작동시작", required: false
        input "endTime", "time", title: "자동화 작동종료", required: false
    }
}

def optionPage()
{
    dynamicPage(name: "optionPage", title: "")
    {
        section("그리고 아래 옵션을 적용합니다(미 설정시 적용되지 않음)") {
            input "light_meter", "capability.illuminanceMeasurement", title: "조도 센서", required: false, multiple: false, submitOnChange: true
            if(light_meter)
            {
                input "lux_max", "number", title: "기준 조도값", required: true, 
                    defaultValue: "30"
            }
            // 이 옵션들은 스위치를 켤때만 적용
            input "stay", "number", required: true, title: "동작 조건 변경 후 꺼짐지연시간(초)", defaultValue: "0"
            input "useManualMode", "bool", title: "수동모드 활성화", defaultValue: true, required: true
            input "enableLog", "bool", title: "로그활성화", required: true, defaultValue: true
        }
        timeInputs()
        
        section("자동화 on/off")
        {
            input "enable", "bool", title: "활성화", required: true, defaultValue: true
        }   
        
        if (!overrideLabel) {
            // if the user selects to not change the label, give a default label
            def l = main_switch.displayName + ": Sensor Switch"
            log.debug "will set default label of $l"
            app.updateLabel(l)
        }
        section("자동화 이름") {
        	if (overrideLabel) {
            	label title: "자동화 이름을 입력하세요", defaultValue: app.label, required: false
            }
            else
            {
            	paragraph app.label
            }
            input "overrideLabel", "bool", title: "이름 수정", defaultValue: "false", required: "false", submitOnChange: true
        }
    }
}



private reactionValues(attributeName) {
    switch(attributeName) {
        case "switch":
            return ["on":"켜기","off":"끄기","toggle":"켜거나 끄기"]
        default:
            return ["UNDEFINED"]
    }
}

private attributeValues(attributeName) {
    switch(attributeName) {
        case "switch":
            return ["on":"켜짐","off":"꺼짐"]
        case "contactSensor":
            return ["open":"열림","closed":"닫힘"]
        case "motionSensor":
            return ["active":"감지됨","inactive":"감지되지않음"]
        case "waterSensor":
            return ["wet":"젖음","dry":"마름"]
        case "button":
        	return ["pushed":"누름","double":"두번누름","held":"길게누름"]
        default:
            return ["UNDEFINED"]
    }
}

private getDeviceAction(name) {
	switch(name) {
    	case "motion":
        	return motionSensorAction
		case "contact":
        	return contactSensorAction
		case "switch":
        	return switchAction
		case "water":
        	return waterSensorAction
    }
}

private sensorActions(name) {
    switch(name) {
        case "switch":
            return "switch"
        case "contactSensor":
            return "contact"
        case "motionSensor":
            return "motion"
        case "waterSensor":
            return "water"
        case "button":
        	return "button"
        default:
            return ["UNDEFINED"]
    }
}

def uninstalled() {
	log("uninstall")
    //deleteDevice()
}

def installed() {
	log("install")
    initialize()
}

def updated() {
	log("updated")
    unsubscribe()
    initialize()
}

def initialize()
{    
	if(!enable) return
	// if the user did not override the label, set the label to the default
    
    if (!overrideLabel) {
        app.updateLabel(app.label)
    }
    
    // 각각의 디바이스 타입에 따라 subscribe
    subscribe(contactSensor, "contact", eventHandler)
    subscribe(motionSensor, "motion", eventHandler)
    subscribe(switches, "switch", eventHandler)
    subscribe(waterSensor, "water", eventHandler)
    
    //def action = sensorActions(sensorType)
    //log("sensor: $sensor , action : $action , reactionValue : $reactionValue")
	//subscribe(sensorList, sensorActions(sensorType), eventHandler)
    //subscribe(sensorList[0], sensorActions("motionSensor"), eventHandler)
    
    //if(light_meter != null)
    //{
    //	subscribe(light_meter, "illuminance", lux_change_handler)
   	//}
    if(main_switch != null) {
        //subscribe(light, "switch.on", switch_on_handler)
        subscribe(main_switch, "switch.off", switch_off_handler)
        subscribe(main_switch, "switch.on", switch_on_handler)
    }
    
    state.on_time = now()
    state.off_time = now()
    state.autoMode = false
    
    state.initialize = true
    
    log("init finish")
}

def isAllDevicesConditionCheck(forOff)
{
	def ret = true
    
    if(contactSensor) {
		contactSensor.each {
        	if(forOff) {
            	if(it.currentState("contact").value == contactSensorAction)
                    ret = false
            }
            else {
                if(it.currentState("contact").value != contactSensorAction)
                    ret = false
			}
        }
	}
    if(motionSensor) {
    	motionSensor.each {
        	if(forOff) {
            	if(it.currentState("motion").value == motionSensorAction)
            		ret = false
            }
            else {
            	if(it.currentState("motion").value != motionSensorAction)
            		ret = false
			}
        }
    }
    if(switches) {
    	switches.each {
        	if(forOff) {
            	if(it.currentState("switch").value == switchAction)
            		ret = false
            }
            else {
            	if(it.currentState("switch").value != switchAction)
            		ret = false
            }
        	
        }
    }
    if(waterSensor) {
    	waterSensor.each {
        	if(forOff) {
            	if(it.currentState("water").value == waterSensorAction)
            		ret = false
            }
            else {
            	if(it.currentState("water").value != waterSensorAction)
            		ret = false
            }
        }
    }
    return ret
}

def eventHandler(evt)
{    
    def triggerCondition = getDeviceAction(evt.name)
    
    log("$evt.name : $evt.value : $triggerCondition")
        
	if (evt.value == triggerCondition)
    {
    	if(isAllDevices && !isAllDevicesConditionCheck(false)) return
        
    	def isBetween = true
        if(null != startTime && null != endTime) { isBetween = timeOfDayIsBetween(startTime, endTime, new Date(), location.timeZone) }
        log("between: $isBetween")
    	if(isBetween)
        {
        	unschedule(switchOff)
            
        	log("main switch: " + main_switch.currentState("switch").value)
            log("light_meter: " + light_meter)
			// 켜짐 작동
            if(main_switch.currentSwitch == "off" &&
               (light_meter == null || (light_meter != null && light_meter.currentIlluminance <= lux_max)))
            {
                state.autoMode = true
                log("set AutoMode")
                switchOn()
            }
            log("main switch on")
        }
    }
    else if(evt.value != triggerCondition)
    {
        if(state.autoMode == true)
        {
        	log.debug "isAllDeviceOff: $isAllDevices_off"
            log.debug "check result: " + isAllDevicesConditionCheck()
            //꺼짐 작동
            if(isAllDevices_off && !isAllDevicesConditionCheck(true)) return
			
            log("scheduled off : $stay seconds")
            if(0 == stay) runIn(stay, switchOff, [overwrite: true])
            else schedule(now() + (stay *1000), switchOff)
            //runIn(stay, switchOff, [overwrite: true])
        }
    }
}

def switchOff() {
	if(true == useManualMode && false == state.autoMode) 
    	return
    
    log("switchOff")
    main_switch.off()
    if(null != sub_switch)
    	sub_switch.off()
}

def switchOn() {
	log("switchOn")
    // 지연 꺼짐을 덮어쓰기 위해 스위치 켜기 전 호출을 한번 해준다
    //runIn(0, switchOff, [overwrite: true])
    //unschedule(switchOff)
    main_switch.on()
    if(null != sub_switch)
    	sub_switch.on()
        
	state.on_time = now()
}

def switch_on_handler(evt) {
    log("switch_on_handler called: $evt")
}

def switch_off_handler(evt) {
    log("switch_off_handler called: $evt")
    state.autoMode = false;
}

def log(msg)
{
	if(enableLog != null && enableLog == true)
    {
    	log.debug msg
    }
}