package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.epochta.Phones;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;

@Log4j2
@PropertySource("classpath:ePochta.properties")
@Component
public class EpochtaApi {
    private RequestBuilder reqBuilder;
    private @Value("${epochta.login}")String login;
    private @Value("${epochta.password}")String password;
    private @Value("${epochta.URL}")String URL;

    @PostConstruct
    private void init() {
        reqBuilder = new RequestBuilder(URL);
    }


    public String getStatus(String msgId){
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        request=request.concat("<SMS><operations><operation>GETSTATUS</operation></operations>");
        request=request.concat("<authentification>");
        request=request.concat("<username>"+this.login+"</username>");
        request=request.concat("<password>"+this.password+"</password>");
        request=request.concat("</authentification>");
        request=request.concat("<statistics>");
        request=request.concat("<messageid>"+msgId+"</messageid>");
        request=request.concat("</statistics>");
        request=request.concat("</SMS>");
        return this.reqBuilder.doXMLQuery(request);
    }

    public String getPrice(String text, Map<String, String> phones){
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        request=request.concat("<SMS>");
        request=request.concat("<operations>  ");
        request=request.concat("<operation>GETPRICE</operation>");
        request=request.concat("</operations> ");
        request=request.concat("<authentification>");
        request=request.concat("<username>"+this.login+"</username>");
        request=request.concat("<password>"+this.password+"</password>");
        request=request.concat("</authentification>");
        request=request.concat("<message>");
        request=request.concat("<sender>SMS</sender>");
        request=request.concat("<text>"+text+"</text>");
        request=request.concat("</message>");
        request=request.concat("<numbers>");
        for (Map.Entry entry : phones.entrySet()) {
            request=request.concat("<number messageID=\""+entry.getKey()+"\">"+entry.getValue()+"</number>");
        }
        request=request.concat("</numbers>");
        request=request.concat("</SMS>");
        return this.reqBuilder.doXMLQuery(request);
    }

    public String getBalance(){
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        request=request.concat("<SMS>");
        request=request.concat("<operations>");
        request=request.concat("<operation>BALANCE</operation>");
        request=request.concat("</operations>");
        request=request.concat("<authentification>");
        request=request.concat("<username>"+this.login+"</username>");
        request=request.concat("<password>"+this.password+"</password>");
        request=request.concat("</authentification> ");
        request=request.concat("</SMS>");
        return this.reqBuilder.doXMLQuery(request);
    }

    public String sendSms(String sender, String text, ArrayList<me.exrates.model.epochta.Phones> phones){
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        request=request.concat("<SMS>");
        request=request.concat("<operations>");
        request=request.concat("<operation>SEND</operation>");
        request=request.concat("</operations>");
        request=request.concat("<authentification>");
        request=request.concat("<username>"+this.login+"</username>");
        request=request.concat("<password>"+this.password+"</password>");
        request=request.concat("</authentification>");
        request=request.concat("<message>");
        request=request.concat("<sender>"+sender+"</sender>");
        request=request.concat("<text>"+text+"</text>");
        request=request.concat("</message>");
        request=request.concat("<numbers>");
        for (Phones phone : phones) {
            request=request.concat("<number");
            if(phone.getIdMessage().length()>0) request=request.concat(" messageID=\""+phone.getIdMessage()+"\"");
            if(phone.getVaraibles().length()>0) request=request.concat(" variables=\""+phone.getVaraibles()+"\"");
            request=request.concat(">");
            request=request.concat(phone.getPhone());
            request=request.concat("</number>");
        }

        request=request.concat("</numbers>");
        request=request.concat("</SMS>");
        return this.reqBuilder.doXMLQuery(request);
    }

    public String getValueFromXml(String xml, String elementName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8.name())));
        doc.getDocumentElement().normalize();
        NodeList nList = doc.getElementsByTagName("RESPONSE");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                return eElement.getElementsByTagName(elementName).item(0).getTextContent();
            }
        }
        return null;
    }
}
