#for /r src %i in (*.java) do javac %i -sourcepath src -d bin
#javac src\\*.java -d bin

dir *.java /s /b > FilesList.txt
mkdir bin
javac -cp ".\lib\lwjgl_natives\windows\lwjgl.dll;.\lib\lwjgl_natives\windows\lwjgl64.dll;.\lib\slick2d_jars\*" -d .\bin @FilesList.txt
del FilesList.txt

#jar cvfe .\bin\game.jar com.jackson.cyberpunk.Game .\bin\* .\lib\lwjgl_natives\windows\lwjgl.dll;.\lib\lwjgl_natives\windows\lwjgl64.dll;.\lib\slick2d_jars\*