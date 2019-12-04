package com.chatting.modelo;
import java.io.Serializable;
public class Mensaje implements Serializable {
  private String to;
  private String from;
  private String message;

  public Mensaje(String from,String to,String message){
    this.to = to;
    this.from = from;
    this.message = message;

  }
  public Mensaje(String from,String message){
    this.from =from;
    this.message = message;
    this.to = null;
  }

  public String getReceiver(){
    return this.to;
  }
  public String getEmiter(){
    return this.from;
  }
  public String getMessage(){
    return this.message;
  }

}