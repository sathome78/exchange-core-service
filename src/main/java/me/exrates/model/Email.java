package me.exrates.model;

import lombok.ToString;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@ToString
public class Email {

	private String to;
	private String from;
	private String message;
	private String subject;
	private List<Attachment> attachments = new ArrayList<>();




	public static class Attachment {
		private String name;
		private Resource resource;
		private String contentType;

		public Attachment() {
		}

		public Attachment(String name, Resource resource, String contentType) {
			this.name = name;
			this.resource = resource;
			this.contentType = contentType;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Resource getResource() {
			return resource;
		}

		public void setResource(Resource resource) {
			this.resource = resource;
		}

		public String getContentType() {
			return contentType;
		}

		public void setContentType(String contentType) {
			this.contentType = contentType;
		}
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public void addAttachment(Attachment attachment) {
		attachments.add(attachment);
	}
}
