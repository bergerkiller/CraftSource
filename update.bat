@echo off
set zip_MC-Dev=mc-dev.zip
set zip_Bukkit=bukkit.zip
set zip_CraftBukkit=craftbukkit.zip
color 2
rem --------------------- Downloading source ----------------------
wget "https://github.com/Bukkit/mc-dev/archive/master.zip" -O "%zip_MC-Dev%" --no-check-certificate
wget "https://github.com/Bukkit/Bukkit/archive/master.zip" -O "%zip_Bukkit%" --no-check-certificate
wget "https://github.com/Bukkit/CraftBukkit/archive/master.zip" -O "%zip_CraftBukkit%" --no-check-certificate
rem ----------------------------------------------------------------
echo Deleting old source...
rem ---------------------------Deleting old source -----------------
rmdir /s /q net.minecraft.server
rmdir /s /q org.bukkit
rmdir /s /q org.bukkit.craftbukkit
rmdir /s /q tmp
rem ----------------------------------------------------------------
echo Extracting...
rem ---------------------------Extracting --------------------------
7za x -y -r -o"tmp" "%zip_MC-Dev%" "mc-dev-master\net\minecraft\server"
mkdir "tmp\CraftBukkit-master\src\main\java\net\minecraft"
move "tmp\mc-dev-master\net\minecraft\server" "tmp\CraftBukkit-master\src\main\java\net\minecraft\server"
7za x -y -r -o"tmp" "%zip_CraftBukkit%" "CraftBukkit-master\src\main\java\net\minecraft\server"
7za x -y -r -o"tmp" "%zip_CraftBukkit%" "CraftBukkit-master\src\main\java\org\bukkit\craftbukkit"
7za x -y -r -o"tmp" "%zip_Bukkit%" "Bukkit-master\src\main\java\org\bukkit"
del "%zip_MC-Dev%"
del "%zip_Bukkit%"
del "%zip_CraftBukkit%"
rem ----------------------------------------------------------------
echo Creating file structure...
rem --------------------Creating file structure-----------------------
move "tmp\CraftBukkit-master\src\main\java\net\minecraft\server" "net.minecraft.server"
move "tmp\CraftBukkit-master\src\main\java\org\bukkit\craftbukkit" "org.bukkit.craftbukkit"
move "tmp\Bukkit-master\src\main\java\org\bukkit" "org.bukkit"
rmdir /s /q tmp
rem ----------------------------------------------------------------
cls
echo Source code has been updated!
pause
exit