/**
 *  Copyright 2015 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Momentary Button Tile
 *
 *  Author: Mark Turner <mark@kram.org>
 *
 *	Date: 2017-12-25	Initial version, based on RM Bridge Switch LAN by beckyricha BeckyR
 *	Date: 2017-12-25	Add ability to send multiple commands using space separated list of commands for device.deviceNetworkId
 */
metadata {
	definition (name: "RM Pro Send Button", namespace: "kram", author: "MarkT") {
		capability "Actuator"
		capability "Switch"
		capability "Momentary"
		capability "Sensor"
	}

	// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles(scale: 2){
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState("off", label: 'Send', action: "momentary.push", backgroundColor: "#ffffff", nextState: "on")
				attributeState("on", label: 'Sending', action: "momentary.push", backgroundColor: "#00a0dc")
			}	
		}
		main "switch"
		details "switch"
	}
}

def parse(String description) {
}

def push() {
	sendEvent(name: "switch", value: "on", isStateChange: true, displayed: false)
	sendEvent(name: "switch", value: "off", isStateChange: true, displayed: false)
	sendEvent(name: "momentary", value: "pushed", isStateChange: true)
    def listCodes = device.deviceNetworkId.tokenize(' ')
    def hubActions = []
    for (String code : listCodes) {
	    hubActions.add(sendCode(code))
    }
    return hubActions
}

private sendCode(toReplace) {
    def url="192.168.2.132:7474"
    def userpassascii= "admin:bridge"
    def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
	def replaced = toReplace.replaceAll(' ', '%20')
 	def hubaction = new physicalgraph.device.HubAction(
		method: "GET",
		path: "/code/$replaced",
		headers: [HOST: "${url}", AUTHORIZATION: "${userpass}"],
	)
	return hubaction
}

def on() {
	push()
}

def off() {
	push()
}