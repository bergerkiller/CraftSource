@echo off
color 2
rem --------------------- Downloading source ----------------------
wget "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar" -O "BuildTools.jar" --no-check-certificate
rem ----------------------------------------------------------------
echo Deleting old source...
rem ---------------------------Deleting old source -----------------
rmdir /s /q net.minecraft.server
rmdir /s /q org.bukkit
rmdir /s /q org.bukkit.craftbukkit
rmdir /s /q org.spigotmc
rem ---------------------------Running BuildTools -----------------
echo Patching...
java -jar BuildTools.jar
rem ----------------------------------------------------------------
echo Extracting...
rem ---------------------------Extracting --------------------------
move "Spigot\Spigot-API\src\main\java\org\bukkit" "org.bukkit"
move "Spigot\Spigot-API\src\main\java\org\spigotmc" "org.spigotmc"

move "Spigot\Spigot-Server\src\main\java\net\minecraft\server" "net.minecraft.server"
move "Spigot\Spigot-Server\src\main\java\org\bukkit\craftbukkit" "org.bukkit.craftbukkit"
rem -------------------- Copy into existing folder------------------
xcopy /S "Spigot\Spigot-Server\src\main\java\org\spigotmc" "org.spigotmc"
rem ----------------------------------------------------------------
echo Source code has been updated!
pause
exit