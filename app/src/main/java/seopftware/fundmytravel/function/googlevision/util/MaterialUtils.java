//package seopftware.fundmytravel.function.googlevision.util;
//
//import android.content.Context;
//import android.util.Log;
//import android.util.Xml;
//
//import org.xmlpull.v1.XmlPullParser;
//import org.xmlpull.v1.XmlSerializer;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
//import java.util.zip.ZipInputStream;
//
///**
// * Created by MSI on 2018-01-17.
// */
//
//public class MaterialUtils {
//
//    // Assets에서 파일의 압축을 푼다.
//    // assetName (압축 패키지 파일 이름)
//    // outputDirectory 출력 디렉토리
//
//    public static void unZip(Context context, String assetName, String outputDirectory) throws IOException {
//
//        // 압축풀기 대상 디렉토리 만들기
//        File file = new File(outputDirectory);
//
//        // 대상 디렉토리가 존재하지 않으면 새로 생성
//        if(!file.exists()) {
//            file.mkdirs(); // 폴더 생성
//        }
//
//        InputStream inputStream = null;
//
//        // zip 파일 열기
//        inputStream = context.getAssets().open(assetName);
//        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
//        ZipEntry zipEntry = zipInputStream.getNextEntry(); // 진입점 읽기
//        byte[] buffer = new byte[1024 * 1024]; // 1MB buffer 사용, 바이트 수 추출
//
//        int count = 0;
//
//        // 진입점이 비어 있으면 아카이브의 모든 파일과 디렉토리가 탐색됩니다.
//        while (zipEntry!=null) {
//
//            // 디렉토리 인 경우
//            if(zipEntry.isDirectory()) {
//                file = new File(outputDirectory + File.separator + zipEntry.getName());
//                file.mkdir();
//            }
//
//            // 파일 인 경우
//            else {
//                file = new File(outputDirectory + File.separator + zipEntry.getName());
//
//                // 파일 만들기
//                file.createNewFile();
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//
//                while((count = zipInputStream.read(buffer)) > 0) {
//                    fileOutputStream.write(buffer, 0, count);
//                }
//
//                fileOutputStream.close();
//            }
//            // 다음 파일 항목으로 이동
//            zipEntry = zipInputStream.getNextEntry();
//        }
//        zipInputStream.close();
//
//    }
//
//    /**
//     * Unzip a zip file.  Will overwrite existing files.
//     *
//     * @param zipFile Full path of the zip file you'd like to unzip.
//     * @param folderPath Full path of the directory you'd like to unzip to (will be created if it doesn't exist).
//     * @throws IOException
//     */
//    // zipfile 파일을 folderPath 디렉토리로 추출한다. (압축 해제)
//    public static int upZipFile(File zipFile, String folderPath) throws IOException {
//        ZipFile zfile = new ZipFile(zipFile); // This class is used to read entries from a zip file.
//        Enumeration zList = zfile.entries();
//        ZipEntry ze = null;
//        byte[] buf = new byte[1024];
//
//        while (zList.hasMoreElements()) {
//            ze = (ZipEntry)zList.nextElement();
//
//            if(ze.isDirectory()) {
//                Log.d("unZipFile", "ze.getName() = " + ze.getName());
//                String dirstr = folderPath + ze.getName();
//
//                dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
//                Log.d("unZipFile", "str = " + dirstr);
//
//                File f= new File(dirstr);
//                f.mkdir();
//                continue;
//            }
//
//            Log.d("unZipFile", "ze.getName() = " + ze.getName());
//            OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
//            InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
//
//            int readLen = 0;
//
//            while ((readLen = is.read(buf, 0, 1024))!=-1) {
//                os.write(buf, 0, readLen);
//            }
//
//            is.close();
//            os.close();
//        }
//
//        zfile.close();
//        Log.d("unZipFile", "finish");
//        return 0;
//    }
//
//    /**
//     루트 디렉토리가 주어지면 실제 파일 이름에 해당하는 상대 경로를 리턴하십시오.
//     * @param baseDir 루트 디렉토리를 지정합니다.
//     * @param absFileName ZipEntry에있는 이름의 상대 경로 이름
//    java.io.File 파일을 돌려 준다
//     */
//    public static File getRealFileName(String baseDir, String absFileName){
//        String[] dirs=absFileName.split("/");
//        File ret=new File(baseDir);
//        String substr = null;
//        if(dirs.length>1){
//            for (int i = 0; i < dirs.length-1;i++) {
//                substr = dirs[i];
//                try {
//                    //substr.trim();
//                    substr = new String(substr.getBytes("8859_1"), "GB2312");
//
//                } catch (UnsupportedEncodingException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                ret=new File(ret, substr);
//
//            }
//            Log.d("upZipFile", "1ret = "+ret);
//            if(!ret.exists())
//                ret.mkdirs();
//            substr = dirs[dirs.length-1];
//            try {
//                //substr.trim();
//                substr = new String(substr.getBytes("8859_1"), "GB2312");
//                Log.d("upZipFile", "substr = "+substr);
//            } catch (UnsupportedEncodingException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//            ret=new File(ret, substr);
//            Log.d("upZipFile", "2ret = "+ret);
//            return ret;
//        }
//        return ret;
//    }
//
//
//    /**
//     * 재료 패키지를 추출하고 자동으로 구성 파일을 생성합니다.
//     */
//    public static void unZipMaterials(Context context , String fileName , int materialType){
//        try{
//            String typeName = getMaterialDescription(materialType);
//            // Android / data / package 이름 / 파일 / 디렉토리 가져 오기
//            File dir = context.getExternalFilesDir(null);
//            if(!dir.exists()){
//                dir.mkdirs();
//            }
//            // 적절한 카테고리 디렉토리에 압축을 풉니 다.
//
//            MaterialUtils.unZip(context , fileName+".zip" , dir.getAbsolutePath()+"/"+typeName);
//
//            // 트래버스가 자동으로 생성 된 구성 파일
//            File file = new File(dir.getAbsolutePath()+"/"+ typeName +"/"+fileName);
//            File[] files = file.listFiles();
//            ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
//            for(int i=0;i<files.length;i++){
//                if(!files[i].getName().toString().startsWith("thumbnail")){
//                    HashMap<String, String> map = new HashMap<String, String>();
//                    map.put("name", files[i].getName());// 원래 이름
//                    map.put("thumbnailname", "thumbnail_"+files[i].getName());// 미리보기 이미지 이름
//                    list.add(map);
//                }
//            }
//            MaterialUtils.writeXML(list, dir.getAbsolutePath() + "/"+ typeName +"/"+fileName+"/");
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * 번들 카테고리 가져 오기
//     */
//    public static String getMaterialDescription(int materialType){
//        String description = null;
//        switch (materialType){
//            case 1:
//                description = "decorate";
//                break;
//            case 2:
//                description = "mode";
//                break;
//            case 3:
//                description = "art";
//                break;
//            case 4:
//                description = "cover";
//                break;
//            case 5:
//                description = "cute";
//                break;
//        }
//        return description;
//    }
//
//    /**
//     * xml 파일을 파싱합니다.
//     */
//    public static ArrayList<HashMap<String,String>> parseXML(InputStream inStream , String packageName) {
//
//        XmlPullParser parser = Xml.newPullParser();
//
//        try {
//            parser.setInput(inStream, "UTF-8");
//            int eventType = parser.getEventType();
//
//            HashMap map = null;
//            ArrayList<HashMap<String,String>> list = null;
//
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                switch (eventType) {
//                    case XmlPullParser.START_DOCUMENT:// 문서 시작 이벤트, 데이터 초기화 가능
//                        list = new  ArrayList<HashMap<String,String>>();
//                        break;
//
//                    case XmlPullParser.START_TAG://요소 이벤트 시작
//                        String name = parser.getName();
//                        if (name.equalsIgnoreCase("material")) {
//                            map = new HashMap<String,String>();
//                        } else if (map != null) {
//                            if (name.equalsIgnoreCase("name")) {
//                                map.put("name",packageName+"/"+parser.nextText());// Text 요소가 추적되면 값을 반환합니다.
//                            } else if (name.equalsIgnoreCase("thumbnailname")) {
//                                map.put("thumbnailname",packageName+"/"+parser.nextText());
//                            }
//                        }
//                        break;
//
//                    case XmlPullParser.END_TAG://요소 끝 이벤트
//                        if (parser.getName().equalsIgnoreCase("material") && map != null) {
//                            list.add(map);
//                            map = null;
//                        }
//
//                        break;
//                }
//
//                eventType = parser.next();
//            }
//
//            inStream.close();
//            return list;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    /**
//     * xml 파일 만들기
//     */
//    public static boolean writeXML(ArrayList<HashMap<String,String>> data, String localDir) {
//        boolean bFlag = false;
//        FileOutputStream fileos = null;
//
//        File newXmlFile = new File(localDir + "materials.xml");
//        try {
//            if (newXmlFile.exists()) {
//                bFlag = newXmlFile.delete();
//            } else {
//                bFlag = true;
//            }
//
//            if (bFlag) {
//
//                if (newXmlFile.createNewFile()) {
//                    fileos = new FileOutputStream(newXmlFile);
//
//                    // we create a XmlSerializer in order to write xml data
//                    XmlSerializer serializer = Xml.newSerializer();
//
//                    // we set the FileOutputStream as output for the serializer,
//                    // using UTF-8 encoding
//                    serializer.setOutput(fileos, "UTF-8");
//
//                    // <?xml version=”1.0″ encoding=”UTF-8″>
//                    // Write <?xml declaration with encoding (if encoding not
//                    // null) and standalone flag (if standalone not null)
//                    // This method can only be called just after setOutput.
//                    serializer.startDocument("UTF-8", null);
//
//                    // start a tag called "materials"
//                    serializer.startTag(null, "materials");
//                    for (HashMap<String,String> map : data) {
//                        serializer.startTag(null, "material");
//                        serializer.startTag(null, "name");
//                        serializer.text(map.get("name"));
//                        serializer.endTag(null, "name");
//                        serializer.startTag(null, "thumbnailname");
//                        serializer.text(map.get("thumbnailname"));
//                        serializer.endTag(null, "thumbnailname");
//                        serializer.endTag(null, "material");
//                    }
//                    serializer.endTag(null, "materials");
//                    serializer.endDocument();
//
//                    // write xml data into the FileOutputStream
//                    serializer.flush();
//                    // finally we close the file stream
//                    fileos.close();
//                }
//            }
//        } catch (Exception e) {
//        }
//        return bFlag;
//    }
//}
