package me.exrates.model.form;

/**
 * Created by OLEG on 21.10.2016.
 */
public class NewsEditorCreationForm {
    private Integer id;
    private String title;
    private String brief;
    private String content;
    private String date;
    private String resource;
    private String newsVariant;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getNewsVariant() {
        return newsVariant;
    }

    public void setNewsVariant(String newsVariant) {
        this.newsVariant = newsVariant;
    }

    @Override
    public String toString() {
        return "NewsEditorCreationForm{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", brief='" + brief + '\'' +
                ", content='" + content + '\'' +
                ", date=" + date +
                ", resource='" + resource + '\'' +
                ", newsVariant='" + newsVariant + '\'' +
                '}';
    }
}
