/*
 * Wise-Saying Smartthings Device Handler (Smartthings API)
 *
 *
 * 
 *
 */
 
import groovy.json.JsonSlurper

metadata {
    definition(name: "Wise Saying", namespace: "oukene", author: "oukene") {
        capability "SpeechSynthesis"
    }

    command "wiseSaying"
    command "playTopMusic"

    preferences {
    input name: "key", type: "text", title: "api key", required: true
    input name: "logEnable", type: "bool", title: "Enable debug logging", defaultValue: true
    }
    
    tiles {
        valueTile("오늘의 명언", "", decoration: "flat", width: 4, height: 2) {
            state "default", label:"ㅁㄴㅇㅁㄴㅁㄴㅇ"
        }
    }
}

def updated() {
    log.info "updated..."
    log.warn "debug logging is: ${logEnable == true}"
    if (logEnable) runIn(1800, logsOff)
}

def logsOff() {
    log.warn "debug logging disabled..."
    device.updateSetting("logEnable", [value: "false", type: "bool"])
}

def parse(String description) {
    if (logEnable) log.debug(description)
}

def buildParms(String capability_, String command_, arg_ = ""){
    def builder = new groovy.json.JsonBuilder()
    def c
    
    if (arg_ instanceof List){
        c = [component:"main", capability:capability_, command:command_, arguments:arg_.collect()]
    }
    else if(arg_ != ""){
        def d = [arg_]
        c = [component:"main", capability:capability_, command:command_, arguments:d.collect()]
    }
    else{
        c = [component:"main", capability:capability_, command:command_]
    }
    
    builder commands:[c]

    def params = [
        uri: "https://api.smartthings.com/v1/devices/" + deviceid + "/commands",
        headers: ['Authorization' : "Bearer " + token],
        body: builder.toString()
    ]
    log.debug(builder.toString())
    return params
}

def post(params){
    try {
        httpPostJson(params) { resp ->
            //if (resp.success) {
            //}
            if (logEnable)
                if (resp.data) log.debug "${resp.data}"
        }
    } catch (Exception e) {
        log.warn "Call to on failed: ${e.message}"
    }
}

def get(params){
    try {
        httpGet(params) { resp ->
            //if (resp.success) {
            //}
            def results = new groovy.json.JsonSlurper().parseText("${resp.data}")
            
 			log.debug "${results.result}"
        }
    } catch (Exception e) {
        log.warn "Call to on failed: ${e.message}"
    }
}


def speak(speechText){
    def params = buildParms("speechSynthesis", "speak", speechText)
    post(params)
}

def setVolume(vol_){
    def params = buildParms("audioVolume", "setVolume", vol_)
    post(params)
}

def playNews(){
    def params = buildParms("samsungim.bixbyContent", "bixbyCommand", "news")
    post(params)    
}

def playWeather(){
    def params = buildParms("samsungim.bixbyContent", "bixbyCommand", "weather")
    post(params)    
}

def playTopMusic(){
    get("https://api.milo.tk//v2/advice?key=c9653bf2c2674611a50d2f7d367868b8")
}

def commandBixby(commandStr_){
    def params = buildParms("samsungim.bixbyContent", "bixbyCommand", ["search_all", commandStr_])
    post(params)
}