1.

 ```shell
sdk use java 22.3.r17-grl
./mvnw clean package -Pnative -pl lib -am -Dmaven.test.skip=true -f pom.xml
```
2. 
 * java 17: --add-modules jdk.incubator.foreign --enable-native-access=ALL-UNNAMED
 * java 19: --enable-preview --enable-native-access=ALL-UNNAMED
   ![image.png](.attachments/img.png)