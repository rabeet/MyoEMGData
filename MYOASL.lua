scriptId = "com.rabeetfatmi-jeffreydubois.orientationscript"
scriptTitle = "Myo Orientation Data"
scriptDetailsUrl = "" -- comment?
maxRecords = 200 -- edit this to increase/decrease the number of records to grab (onPeriodic() runs every 10ms so 200 means you have 2 seconds to perform your gesture)
currentRecord = 0

function createFile()
	local count = 1
	local name = "OrientationData" .. count .. ".csv"
	while(fileExists(name))
	do
		count=count + 1
		name = "OrientationData" .. count .. ".csv"
	end
	file = io.open(name,"w")
	io.output(file)
	io.write("Roll,Pitch,Yaw,xV,yV,zV,Velocity,xA,yA,zA,Acceleration,Class\n")
	return file
end

function fileExists(name)
	local f=io.open(name,"r")
	if f~=nil 
	then 
		io.close(f) 
		return true 
	else 
		return false
	end
end

file = createFile()
open = true

function onPeriodic()
	if (currentRecord < maxRecords) then
		local currentRoll = myo.getRoll() -- Get an angular value for Myo's orientation about its X axis, i.e. the wearer's arm. Positive roll indicates clockwise rotation (from the point of view of the wearer). (X)
		local currentPitch = myo.getPitch() -- Get an angular value for Myo's orientation about its Y axis. Positive pitch indicates the wearer moving their arm upwards, away from the ground. (Y)
		local currentYaw = myo.getYaw() -- Get an angular value for Myo's orientation about its Z axis. Positive yaw indicates rotation to the wearer's right. (Z)
		local currentGyro = myo.getGyro() -- Get the angular velocity of Myo about its axes, in radians / second.
		local currentAccel = myo.getAccel() -- Get the acceleration of Myo in its own reference frame in units of g
		local a,b,c = myo.getGyro() -- Get the angular velocity of Myo (x, y, z) about its axes, in radians / second.
		local x,y,z = myo.getAccel() -- Get the acceleration of Myo (x, y and z) in its own reference frame in units of g
		io.write(currentRoll .. "," .. currentPitch .. "," .. currentYaw .. "," .. a .. "," .. b .. "," .. c .. "," .. currentGyro .. "," .. x .. "," .. y .. "," .. z .. "," .. currentAccel .. "\n")
		myo.debug(currentRoll .. "," .. currentPitch .. "," .. currentYaw .. "," .. a .. "," .. b .. "," .. c .. "," .. currentGyro .. "," .. x .. "," .. y .. "," .. z .. "," .. currentAccel)
	else
		if (open) then
			open = false
			io.close(file)
			myo.debug("File closed!")
		end
	end
	currentRecord = currentRecord + 1
end

-- while (currentRecord < 500) do -- testing onPeriodic()
-- 	onPeriodic()
-- end