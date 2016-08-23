# -*- coding: utf-8 -*-
#
#This software was developed by employees of the National Institute of
#Standards and Technology (NIST), and others.
#This software has been contributed to the public domain.
#Pursuant to title 15 Untied States Code Section 105, works of NIST
#employees are not subject to copyright protection in the United States
#and are considered to be in the public domain.
#As a result, a formal license is not needed to use this software.
#
#This software is provided "AS IS."
#NIST MAKES NO WARRANTY OF ANY KIND, EXPRESS, IMPLIED
#OR STATUTORY, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTY OF
#MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NON-INFRINGEMENT
#AND DATA ACCURACY.  NIST does not warrant or make any representations
#regarding the use of the software or the results thereof, including but
#not limited to the correctness, accuracy, reliability or usefulness of
#this software.

import util
import sys
import traceback
import DbCollections
import SensorDb
from Defines import SENSOR_ID, TIME_ZONE_KEY
from Defines import STATUS

def getSystemMessages(sensorId):
    util.debugPrint("getSystemMessages " + sensorId)
    query = {SENSOR_ID: sensorId}
    record = DbCollections.getSensors().find_one(query)
    if record is None:
        return {STATUS: "NOK", "StatusMessage": "Sensor not found"}
    cur = DbCollections.getSystemMessages().find({SENSOR_ID:sensorId})
    systemMessages = []
    if cur == None:
        return {STATUS: "OK", "systemMessages": systemMessages}
    else:
        for systemMessage in cur:
            del systemMessage["_id"]
            systemMessages.append(systemMessage)
        return {STATUS: "OK", "systemMessages": systemMessages}