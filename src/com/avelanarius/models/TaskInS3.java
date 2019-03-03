package com.avelanarius.models;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.text.translate.UnicodeUnescaper;

public class TaskInS3 {
    private String name;
    
    private Date updatedAt;
    
    private String opis;
    
    private int iloscPlikow;
    
    private int wersja;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public int getIloscPlikow() {
        return iloscPlikow;
    }

    public void setIloscPlikow(int iloscPlikow) {
        this.iloscPlikow = iloscPlikow;
    }

    public int getWersja() {
        return wersja;
    }

    public void setWersja(int wersja) {
        this.wersja = wersja;
    }
    
    public void fillFromMetadata(Map<String, String> metadata) {
        try {
            if (metadata.containsKey("updatedat")) {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", new Locale("us"));
                this.setUpdatedAt(sdf.parse(metadata.get("updatedat")));
            } else this.setUpdatedAt(Calendar.getInstance().getTime());
        } catch (ParseException ex) {
            Logger.getLogger(TaskInS3.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (metadata.containsKey("nazwa")) this.setName(metadata.get("nazwa"));
        UnicodeUnescaper unescaper = new UnicodeUnescaper();
        if (metadata.containsKey("opis")) this.setOpis(unescaper.translate(metadata.get("opis")));
        if (metadata.containsKey("iloscplikow")) this.setIloscPlikow(Integer.valueOf(metadata.get("iloscplikow")));
        if (metadata.containsKey("wersja")) this.setWersja(Integer.valueOf(metadata.get("wersja")));
    }

    @Override
    public String toString() {
        return "TaskInS3{" + "name=" + name + ", updatedAt=" + updatedAt + ", opis=" + opis + ", iloscPlikow=" + iloscPlikow + '}';
    }
}
