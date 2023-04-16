import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

//test plaintext
//If you want to keep those Spring Boot MVC customizations and make more MVC customizations interceptors formatters view controllers and other features you can add your own Configuration class of type WebMvcConfigurer but without EnableWebMvc If you want to provide custom instances of RequestMappingHandlerMapping RequestMappingHandlerAdapter or ExceptionHandlerExceptionResolver and still keep the Spring Boot MVC customizations you can declare a bean of type WebMvcRegistrations and use it to provide custom instances of those components If you want to take complete control of Spring MVC you can add your own Configuration annotated with EnableWebMvc or alternatively add your own Configuration annotated DelegatingWebMvcConfiguration as described in the Javadoc of EnableWebMvc

public class VigenereCipher {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入明文");
        String m = sc.nextLine();
        System.out.println("请输入密钥");
        String k = sc.nextLine();
        String cipher = encrypt(m.replace(" ", "").toLowerCase(), k);
        System.out.println("加密后：" + cipher);
//        System.out.println(Friedman(cipher));
        decryptCipher(Friedman(cipher), cipher);
    }

    public static String encrypt(String m, String k) {
        char[] result = new char[m.length()];
        int j = 0, z = 0;
        for (int i = 0; i < m.length(); i++) {
            if (m.charAt(i) >= 'a' && m.charAt(i) <= 'z')
                result[z] = (char) (((m.charAt(i) - 'a' + k.charAt(j) - 'a') % 26) + 97);
            else if (m.charAt(i) >= 'A' && m.charAt(i) <= 'Z')
                result[z] = (char) (((m.charAt(i) - 'A' + k.charAt(j) - 'a') % 26) + 65);
            else {
                result[z] = m.charAt(i);
                j--;
            }
            j++;
            if (j >= k.length()) j = 0;
            z++;
        }
        return new String(result);
    }

    public static int Friedman(String ciphertext) {
        int keyLength = 2; // 猜测密钥长度
        double[] Ic; // 重合指数
        ArrayList<String> cipherGroup; // 密文分组
        int resLength = 2;
        double resIc = 0;
        while (keyLength<=10) {
            Ic = new double[keyLength];
            cipherGroup = new ArrayList<>();

            // 1 先根据密钥长度分组
            for (int i = 0; i < keyLength; ++i) {
                StringBuilder tempGroup = new StringBuilder();
                for (int j = 0; i + j * keyLength < ciphertext.length(); ++j) {
                    tempGroup.append(ciphertext.charAt(i + j * keyLength));
                }
                cipherGroup.add(tempGroup.toString());
            }

            // 2 再计算每一组的重合指数
            for (int i = 0; i < keyLength; ++i) {
                String subCipher = cipherGroup.get(i); // 子串
                HashMap<Character, Integer> occurrenceNumber = new HashMap<>(); // 字母及其出现的次数

                // 2.1 初始化字母及其次数键值对
                for (int h = 0; h < 26; ++h) {
                    occurrenceNumber.put((char) (h + 97), 0);
                }

                // 2.2 统计每个字母出现的次数
                for (int j = 0; j < subCipher.length(); ++j) {
                    occurrenceNumber.put(subCipher.charAt(j), occurrenceNumber.get(subCipher.charAt(j)) + 1);
                }

                // 2.3 计算重合指数
                double fm = (double) subCipher.length() * ((double) subCipher.length() - 1);
                for (int k = 0; k < 26; ++k) {
                    double o = (double) occurrenceNumber.get((char) (k + 97));
                    Ic[i] += o * (o - 1);
                }
                Ic[i] /= fm;
            }
            double s = 0;
            for (int i=0;i<keyLength;i++){
                s+=Math.pow(Ic[i]-0.065,2);
            }
            if(resIc == 0){
                resLength = keyLength;
                resIc = s;
            }
            else if(s<resIc){
                resLength = keyLength;
                resIc = s;
            }
            keyLength++;
        }
        System.out.println("密钥长度为：" + resLength);
        return resLength;
    }

    // 再次使用重合指数法确定密钥
    public static void decryptCipher(int keyLength, String ciphertext) {

        int[] key = new int[keyLength];
        ArrayList<String> cipherGroup = new ArrayList<>();
        double[] probability = new double[]{0.082, 0.015, 0.028, 0.043, 0.127, 0.022, 0.02, 0.061, 0.07, 0.002, 0.008,
                0.04, 0.024, 0.067, 0.075, 0.019, 0.001, 0.06, 0.063, 0.091, 0.028, 0.01, 0.023, 0.001, 0.02, 0.001};

        // 1 先根据密钥长度分组
        for (int i = 0; i < keyLength; ++i) {
            StringBuilder temporaryGroup = new StringBuilder();
            for (int j = 0; i + j * keyLength < ciphertext.length(); ++j) {
                temporaryGroup.append(ciphertext.charAt(i + j * keyLength));
            }
            cipherGroup.add(temporaryGroup.toString());
        }

        // 2 确定密钥
        for (int i = 0; i < keyLength; ++i) {
            double MG; // 重合指数
            double resMg = 0;
            int flag; // 移动位置
            int g = 0; // 密文移动g个位置
            HashMap<Character, Integer> occurrenceNumber; // 字母出现次数
            String subCipher; // 子串
            while (true) {
                MG = 0;
                flag = 97 + g;
                subCipher = cipherGroup.get(i);
                occurrenceNumber = new HashMap<>();

                // 2.1 初始化字母及其次数
                for (int h = 0; h < 26; ++h) {
                    occurrenceNumber.put((char) (h + 97), 0);
                }

                // 2.2 统计字母出现次数
                for (int j = 0; j < subCipher.length(); ++j) {
                    occurrenceNumber.put(subCipher.charAt(j), occurrenceNumber.get(subCipher.charAt(j)) + 1);
                }

                for (int k = 0; k < 26; ++k, ++flag) {
                    double p = probability[k];
                    flag = (flag == 123) ? 97 : flag;
                    double f = (double) occurrenceNumber.get((char) flag) / subCipher.length();
                    MG += Math.pow(f-p,2);
                }
                if(resMg == 0){
                    key[i] = g;
                    resMg = MG;
                }
                else if(MG < resMg){
                    key[i] = g;
                    resMg = MG;
                }

                g++;
                if(g>=26){
                    break;
                }
            }
        }

        // 3 打印密钥
        StringBuilder keyString = new StringBuilder();
        for (int i = 0; i < keyLength; ++i) {
            keyString.append((char) (key[i] + 97));
        }
        System.out.println("密钥为: " + keyString);

        // 4 解密
        StringBuilder plainBuffer = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); ++i) {
            int keyFlag = i % keyLength;
            int change = (int) ciphertext.charAt(i) - 97 - key[keyFlag];
            char plainLetter = (char) ((change < 0 ? (change + 26) : change) + 97);
            plainBuffer.append(plainLetter);
        }
        System.out.println("明文为：\n" + plainBuffer.toString().toLowerCase());
    }
}