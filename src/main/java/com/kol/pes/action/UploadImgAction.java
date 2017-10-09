/*-----------------------------------------------------------

-- PURPOSE

--    接收图片上传的Action

-- History

--	  09-Sep-14  LiZheng  Created.

------------------------------------------------------------*/

package com.kol.pes.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.kol.pes.service.SeqService;
import com.kol.pes.utils.LogUtil;


public class UploadImgAction extends ParentAction {

	private static final long serialVersionUID = 4424087712369555098L;

	@Autowired
	@Qualifier("seqService")
	private SeqService seqService;
	private String imgFilePath;

	@Override
	@Action(value="/uploadImg", results={
			@Result(name="success", location="upload_img.ftl", type="freemarker", params={"contentType", "text/xml"})
	})
	
	public String execute() {
		try {
			imageFileName = fileName;//"kol_"+CommonUtil.formatDateTimeForFileName(Calendar.getInstance()) + getExtention(fileName);
			String pathFile = ServletActionContext.getServletContext().getRealPath(File.separator) + "UploadImages" + File.separator + imageFileName;
			pathFile = pathFile.replace("erp"+File.separator, "");
			LogUtil.log("uploadImg="+pathFile);
			
			File imageFile = new File(pathFile);
			//if(!imageFile.exists()) {
				imageFile.createNewFile();
			//}
			copy(myFile, imageFile);
			
			imgFilePath = pathFile;
		} 
		catch (Exception e) {
			setCode(CODE_FAIL);
			setMessage(String.valueOf(e));
		}
		
		return SUCCESS;
	}
	
	public String getFilePath() {
		return imgFilePath;
	}
	
    private static final int BUFFER_SIZE = 16 * 1024 ;
   
    private File myFile;
//    private String contentType;
    private String fileName;
    private String imageFileName;
    private String caption;
   
//    public void setMyFileContentType(String contentType) {
//        this.contentType = contentType;
//   } 
   
    public void setMyFileFileName(String fileName) {
        this.fileName = fileName;
   } 
       
    public void setMyFile(File myFile) {
        this.myFile = myFile;
   } 
   
    public String getImageFileName() {
        return imageFileName;
   } 
   
    public String getCaption() {
        return caption;
   } 

    public void setCaption(String caption) {
        this.caption = caption;
   } 
    
    public void setTranactionId(String tranactionId) {
    	LogUtil.log("tranactionId="+tranactionId);
    }
   
    private static void copy(File src, File dst) {
        try {
           InputStream in = null ;
           OutputStream out = null ;
            try {                
               in = new BufferedInputStream( new FileInputStream(src), BUFFER_SIZE);
               out = new BufferedOutputStream( new FileOutputStream(dst), BUFFER_SIZE);
               byte [] buffer = new byte [BUFFER_SIZE];
               int i = 0;
               while (( i = in.read(buffer)) > 0 ) {
                   out.write(buffer, 0, i);
               }
           } finally {
                if ( null != in) {
                   in.close();
               } 
                if ( null != out) {
                   out.close();
               } 
           } 
       } catch (Exception e) {
           e.printStackTrace();
       } 
   } 
   
//    private static String getExtention(String fileName) {
//    	LogUtil.log("getExtention():fileName="+fileName);
//        int pos = fileName.lastIndexOf( "." );
//        return fileName.substring(pos);
//   } 


   
}
