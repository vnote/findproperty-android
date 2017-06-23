package com.cetnaline.findproperty.db.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by fanxl2 on 2017/2/7.
 */
@Entity(
	nameInDb = "house_file_download"
)
public class FileDownLoad {

	@Id(autoincrement = true)
	private Long id;

	private String downPath;

	private Integer downloadLength;

	private Integer threadId;

	private String modifiedSince;

	@Generated(hash = 1658798271)
	public FileDownLoad(Long id, String downPath, Integer downloadLength,
			Integer threadId, String modifiedSince) {
		this.id = id;
		this.downPath = downPath;
		this.downloadLength = downloadLength;
		this.threadId = threadId;
		this.modifiedSince = modifiedSince;
	}

	@Generated(hash = 1839439066)
	public FileDownLoad() {
	}

	public Long getId() {
					return this.id;
	}

	public void setId(Long id) {
					this.id = id;
	}

	public String getDownPath() {
					return this.downPath;
	}

	public void setDownPath(String downPath) {
					this.downPath = downPath;
	}

	public Integer getDownloadLength() {
					return this.downloadLength;
	}

	public void setDownloadLength(Integer downloadLength) {
					this.downloadLength = downloadLength;
	}

	public Integer getThreadId() {
					return this.threadId;
	}

	public void setThreadId(Integer threadId) {
					this.threadId = threadId;
	}

	public String getModifiedSince() {
		return this.modifiedSince;
	}

	public void setModifiedSince(String modifiedSince) {
		this.modifiedSince = modifiedSince;
	}



}
