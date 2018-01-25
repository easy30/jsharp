package jsharp.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Vector;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**

 */
public class Sftp {

	private ChannelSftp c = null;
	private Session session = null;
	private JSch jsch = new JSch();

	/**
	 * ����sftp������
	 * 
	 * @param host
	 *            ����
	 * @param port
	 *            �˿�
	 * @param username
	 *            �û���
	 * @param password
	 *            ����
	 * @return
	 * @throws JSchException
	 */
	public void connect(String host, int port, String username, String password)
			throws JSchException {

		jsch.getSession(username, host, port);
		session = jsch.getSession(username, host, port);
		System.out.println("Session created.");
		session.setPassword(password);
		Properties sshConfig = new Properties();
		//sshConfig.put("userauth.gssapi-with-mic", "no");
		sshConfig.put("StrictHostKeyChecking", "no");
		session.setConfig(sshConfig);
		session.connect();
		System.out.println("Session connected.");
		System.out.println("Opening Channel.");
		Channel channel = session.openChannel("sftp");
		channel.connect();
		c = (ChannelSftp) channel;
		System.out.println("Connected to " + host + ".");

		// return sftp;
	}

	public void cd(String directory) throws SftpException {
		c.cd(directory);
	}

	/**
	 * �ϴ��ļ�
	 * 
	 * @param directory
	 *            �ϴ���Ŀ¼
	 * @param localFile
	 *            Ҫ�ϴ����ļ�
	 * @param c
	 * @throws SftpException
	 * @throws FileNotFoundException
	 */
	public void upload(String localFile) throws SftpException,
			FileNotFoundException {

		File file = new File(localFile);
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			c.put(is, file.getName());
		} finally {

			closeObjects(is);

		}

	}

	/**
	 * �����ļ�
	 * 
	 * @param directory
	 *            ����Ŀ¼
	 * @param downloadFile
	 *            ���ص��ļ�
	 * @param saveFile
	 *            ���ڱ��ص�·��
	 * @param c
	 * @throws FileNotFoundException
	 * @throws SftpException
	 */
	public void download(String downloadFile, String saveFile)
			throws FileNotFoundException, SftpException {

		OutputStream os = null;
		try {
			// sftp.cd(directory);
			File file = new File(saveFile);
			os = new FileOutputStream(file);
			c.get(downloadFile, os);
		} finally {
			closeObjects(os);
		}
	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param directory
	 *            Ҫɾ���ļ�����Ŀ¼
	 * @param deleteFile
	 *            Ҫɾ�����ļ�
	 * @param c
	 * @throws SftpException
	 */
	public void delete(String deleteFile) throws SftpException {

		// sftp.cd(directory);
		c.rm(deleteFile);

	}
	
	public void mkdir(String directory) throws SftpException {
		 c.mkdir(directory);
	}

	/**
	 * �г�Ŀ¼�µ��ļ�
	 * 
	 * @param directory
	 *            Ҫ�г���Ŀ¼
	 * @param c
	 * @return
	 * @throws SftpException
	 */
	public Vector<LsEntry> list(String directory) throws SftpException {
		if (directory == null || directory.length() == 0)
			directory = ".";
		return c.ls(directory);
	}

	public void close() {

		c.quit();
		session.disconnect();

	}

	private static void closeObjects(Object... os) {
		for (Object o : os)
			if (o != null)
				try {

					o.getClass().getMethod("close", null).invoke(o, null);
				} catch (Exception e) {

				}
	}
	
	public ChannelSftp getChannelSftp(){
		return c;
	}

	public static void main(String[] args) throws Exception {
		Sftp sf = new Sftp();
		String host = "Sftp.alipay.com";
		int port = 22;
		String username = "tbcaipiao";
		String password = "9Q537A";
		String directory =args[0];// "/home/tbcaipiao/sftp_root/download";
		String uploadFile = "/home/ruixiang.mrx/readme.txt";
		String downloadFile = "readme.txt";
		String saveFile = "/home/ruixiang.mrx/readme1.txt";
		// String deleteFile = "delete.txt";
		sf.connect(host, port, username, password);

		// if(true)return;
		sf.cd(directory);
		 
		//sf.upload(uploadFile);
		//sf.download(downloadFile, saveFile);
		// sf.delete(directory, deleteFile, sftp);

		Vector<LsEntry> v = sf.list(".");
		for (LsEntry e : v) {
			System.out.println(e + "," + e.getAttrs().getSize()+","+e.getAttrs().getMTime());
		}
		
		System.out.println(System.currentTimeMillis());

		sf.close();

		try {
			// c.cd(directory);
			// sftp.mkdir("ss");
			System.out.println("finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}