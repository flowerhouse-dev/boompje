package stamboom.domain;

import java.util.*;
import stamboom.util.StringUtilities;

public class Administratie implements java.io.Serializable {

    //************************datavelden*************************************
    private int nextGezinsNr;
    private int nextPersNr;
    private final List<Persoon> personen;
    private final List<Gezin> gezinnen;

    //***********************constructoren***********************************
    /**
     * er wordt een administratie gecreeerd met 0 personen en dus 0 gezinnen
     * personen en gezinnen die in de toekomst zullen worden gecreeerd, worden
     * elk opvolgend genummerd vanaf 1
     */
    public Administratie() {
        this.nextGezinsNr = 1;
        this.nextPersNr = 1;
        personen = new ArrayList<>();
        gezinnen = new ArrayList<>();
    }

    //**********************methoden****************************************
    /**
     * er wordt een persoon met een gegeven geslacht, met als voornamen vnamen,
     * achternaam anaam, tussenvoegsel tvoegsel, geboortedatum gebdat,
     * geboorteplaats gebplaats en een gegeven ouderlijk gezin gecreeerd; de persoon
     * krijgt een uniek nummer toegewezen de persoon is voortaan ook bij het
     * ouderlijk gezin bekend. Voor de voornamen, achternaam en gebplaats geldt
     * dat de eerste letter naar een hoofdletter en de resterende letters naar
     * een kleine letter zijn geconverteerd; het tussenvoegsel is zo nodig in
     * zijn geheel geconverteerd naar kleine letters; overbodige spaties zijn 
     * verwijderd
     *
     * @param geslacht
     * @param vnamen vnamen.length>0; alle strings zijn niet leeg
     * @param anaam niet leeg
     * @param tvoegsel
     * @param gebdat
     * @param gebplaats niet leeg
     * @param ouderlijkGezin mag de waarde null (=onbekend) hebben
     *
     * @return als de persoon al bekend was (op basis van combinatie van getNaam(),
     * geboorteplaats en geboortedatum), wordt er null geretourneerd, anders de 
     * nieuwe persoon
     */
    public Persoon addPersoon(Geslacht geslacht, String[] vnamen, String anaam,
            String tvoegsel, Calendar gebdat,
            String gebplaats, Gezin ouderlijkGezin) {

        if (vnamen.length == 0) {
            throw new IllegalArgumentException("ten minst 1 voornaam");
        }
        for (String voornaam : vnamen) {
            if (voornaam.trim().isEmpty()) {
                throw new IllegalArgumentException("lege voornaam is niet toegestaan");
            }
        }

        if (anaam.trim().isEmpty()) {
            throw new IllegalArgumentException("lege achternaam is niet toegestaan");
        }

        if (gebplaats.trim().isEmpty()) {
            throw new IllegalArgumentException("lege geboorteplaats is niet toegestaan");
        }
        
        Persoon newperson = new Persoon(this.nextPersNr,vnamen,anaam,tvoegsel,gebdat,gebplaats,geslacht,ouderlijkGezin);
        
        if(this.personen != null)
        {            
            for (Persoon p : this.personen)
            {
                
               
                if(p.getNaam().equals(newperson.getNaam()))
                {
                    
                    if(p.getGebDat().equals(newperson.getGebDat()))
                    {
                        
                        if(p.getGebPlaats().equals(newperson.getGebPlaats()))
                        {
                            return null;
                        }
                        
                    }
                    


                }

            } 
            
        }

        
        this.personen.add(newperson);
        if(ouderlijkGezin != null){
        ouderlijkGezin.breidUitMet(newperson);
        }
        this.nextPersNr++;
        return newperson;
    }

    /**
     * er wordt, zo mogelijk (zie return) een (kinderloos) ongehuwd gezin met
     * ouder1 en ouder2 als ouders gecreeerd; de huwelijks- en scheidingsdatum
     * zijn onbekend (null); het gezin krijgt een uniek nummer toegewezen; dit
     * gezin wordt ook bij de afzonderlijke ouders geregistreerd;
     *
     * @param ouder1
     * @param ouder2 mag null zijn
     *
     * @return null als ouder1 = ouder2 of als de volgende voorwaarden worden
     * overtreden: 1) een van de ouders is op dit moment getrouwd 2) het koppel
     * uit een ongehuwd gezin kan niet tegelijkertijd als koppel bij een ander
     * ongehuwd gezin betrokken zijn anders het gewenste gezin
     */
    public Gezin addOngehuwdGezin(Persoon ouder1, Persoon ouder2) {
        if (ouder1 == ouder2) {
            return null;
        }

        Calendar nu = Calendar.getInstance();
       /* if (ouder1.isGetrouwdOp(nu)  || (ouder2 != null
                && ouder2.isGetrouwdOp(nu))
                || ongehuwdGezinBestaat(ouder1, ouder2)) {
            return null;
        }
        */
        for(Gezin g : gezinnen)
        {
          if(g.getOuder1() == ouder1 && g.isHuwelijkOp(nu))
          {
          return null;
          }
          if(g.getOuder1() == ouder2 && g.isHuwelijkOp(nu) )
          {
          return null;
          }
          if(g.getOuder2() == ouder1 && g.isHuwelijkOp(nu))
          {
          return null;
          }
          if(g.getOuder2() == ouder2 && g.isHuwelijkOp(nu))
          {
          return null;
          }
          if(g.getOuder1() == ouder1 && g.getOuder2() == ouder2)
          {
          return null;
          }
          if(g.getOuder2() == ouder1 && g.getOuder1() == ouder2)
          {
          return null;
          }
        }
        if(ouder1.isGetrouwdOp(nu) || (ouder2 != null && ouder2.isGetrouwdOp(nu)))
        {
        return null;
        }

        Gezin gezin = new Gezin(nextGezinsNr, ouder1, ouder2);
        nextGezinsNr++;
        gezinnen.add(gezin);

        ouder1.wordtOuderIn(gezin);
        if (ouder2 != null) {
            ouder2.wordtOuderIn(gezin);
        }

        return gezin;
    }

    /**
     * Als het ouderlijk gezin van persoon nog onbekend is dan wordt persoon een
     * kind van ouderlijkGezin en tevens wordt persoon als kind in dat gezin
     * geregistreerd; <br>
     * Als de ouders bij aanroep al bekend zijn, verandert er
     * niets
     *
     * @param persoon
     * @param ouderlijkGezin
     */
    public void setOuders(Persoon persoon, Gezin ouderlijkGezin) {
        persoon.setOuders(ouderlijkGezin);
    }

    /**
     * als de ouders van dit gezin gehuwd zijn en nog niet gescheiden en datum
     * na de huwelijksdatum ligt, wordt dit de scheidingsdatum. Anders gebeurt
     * er niets.
     *
     * @param gezin
     * @param datum
     * @return true als scheiding geaccepteerd, anders false
     */
    public boolean setScheiding(Gezin gezin, Calendar datum) {
        return gezin.setScheiding(datum);
    }

    /**
     * registreert het huwelijk, mits gezin nog geen huwelijk is en beide ouders
     * op deze datum mogen trouwen (pas op: ook de toekomst kan hierbij een rol
     * spelen omdat toekomstige gezinnen eerder zijn geregisteerd)
     *
     * @param gezin
     * @param datum de huwelijksdatum
     * @return false als huwelijk niet mocht worden voltrokken, anders true
     */
    public boolean setHuwelijk(Gezin gezin, Calendar datum) {
        return gezin.setHuwelijk(datum);
    }

    /**
     *
     * @param ouder1
     * @param ouder2
     * @return true als dit koppel (ouder1,ouder2) al een ongehuwd gezin vormt
     */
    boolean ongehuwdGezinBestaat(Persoon ouder1, Persoon ouder2) {
        return ouder1.heeftOngehuwdGezinMet(ouder2) != null;
    }

    /**
     * als er al een ongehuwd gezin voor dit koppel bestaat, wordt het huwelijk
     * voltrokken, anders wordt er zo mogelijk (zie return) een (kinderloos)
     * gehuwd gezin met ouder1 en ouder2 als ouders gecreeerd; de
     * scheidingsdatum is onbekend (null); het gezin krijgt een uniek nummer
     * toegewezen; dit gezin wordt ook bij de afzonderlijke ouders
     * geregistreerd;
     *
     * @param ouder1
     * @param ouder2
     * @param huwdatum
     * @return null als ouder1 = ouder2 of als een van de ouders getrouwd is
     * anders het gehuwde gezin
     */
    public Gezin addHuwelijk(Persoon ouder1, Persoon ouder2, Calendar huwdatum) {
        //System.out.println(ouder1.getVoornamen() + " " + ouder2.getVoornamen());
        if(ouder1 == ouder2)
        {
            return null;
        }
        if(!ouder1.kanTrouwenOp(huwdatum) || !ouder2.kanTrouwenOp(huwdatum))
        {
            return null;
        }
        
        if(this.ongehuwdGezinBestaat(ouder1, ouder2) || this.ongehuwdGezinBestaat(ouder2, ouder1))
        { 
            for(Gezin g : ouder1.getAlsOuderBetrokkenIn())
            {
               if(g.getOuder1()==ouder2 || g.getOuder2()==ouder2)
               {
                   setHuwelijk(g,huwdatum);
                   return g;
               }

            }
        
           
        }
        
        
        Gezin newgezin = new Gezin(this.nextGezinsNr,ouder1,ouder2);
        this.nextGezinsNr++;
        newgezin.setHuwelijk(huwdatum);
        this.gezinnen.add(newgezin);
        ouder1.wordtOuderIn(newgezin);
        ouder2.wordtOuderIn(newgezin);
        return newgezin;
    }

    /**
     *
     * @return het aantal geregistreerde personen
     */
    public int aantalGeregistreerdePersonen() {
        return nextPersNr - 1;
    }

    /**
     *
     * @return het aantal geregistreerde gezinnen
     */
    public int aantalGeregistreerdeGezinnen() {
        return nextGezinsNr - 1;
    }

    /**
     *
     * @param nr
     * @return de persoon met nummer nr, als die niet bekend is wordt er null
     * geretourneerd
     */
    public Persoon getPersoon(int nr) {
        //todo opgave 1
        //aanname: er worden geen personen verwijderd
        for (Persoon p : personen)
        {
            if(p.getNr() == nr)
            {
                return p;
            }
        }
        return null;
    }

    /**
     * @param achternaam
     * @return alle personen met een achternaam gelijk aan de meegegeven
     * achternaam (ongeacht hoofd- en kleine letters)
     */
    public ArrayList<Persoon> getPersonenMetAchternaam(String achternaam) {
        ArrayList<Persoon> lijstpersonen = new ArrayList<>();
        for(Persoon p : personen)
        {
            if(p.getAchternaam().equals(StringUtilities.withFirstCapital(achternaam)))
            {
                lijstpersonen.add(p);
            }
        }
        if(lijstpersonen != null)
        {
         return lijstpersonen;
        }
        return null;
    }

    /**
     *
     * @return de geregistreerde personen
     */
    public List<Persoon> getPersonen() {
        ArrayList<Persoon> lijstpersonen = new ArrayList<>();
        for(Persoon p : personen)
        {
            lijstpersonen.add(p);
        }
        return lijstpersonen;
    }

    /**
     *
     * @param vnamen
     * @param anaam
     * @param tvoegsel
     * @param gebdat
     * @param gebplaats
     * @return de persoon met dezelfde initialen, tussenvoegsel, achternaam,
     * geboortedatum en -plaats mits bekend (ongeacht hoofd- en kleine letters),
     * anders null
     */
    public Persoon getPersoon(String[] vnamen, String anaam, String tvoegsel,
            Calendar gebdat, String gebplaats) {
        for (Persoon p : personen)
        {
          
            
            String initialen = new String();
            for(String s : vnamen)
            {
                s = StringUtilities.withFirstCapital(s);
                initialen += s.substring(0,1) + ".";
            }
            
            
            
           if (p.getInitialen().equals(initialen) && StringUtilities.withFirstCapital(p.getAchternaam()).equals(StringUtilities.withFirstCapital(anaam)) && StringUtilities.withFirstCapital(p.getTussenvoegsel()).equals(StringUtilities.withFirstCapital(tvoegsel)) && p.getGebDat().equals(gebdat) && StringUtilities.withFirstCapital(p.getGebPlaats()).equals(StringUtilities.withFirstCapital(gebplaats)))
            {
                return p;
            }
        }
        return null;
    }

    /**
     *
     * @return de geregistreerde gezinnen
     */
    public List<Gezin> getGezinnen() {
        return null;
    }

    /**
     *
     * @param gezinsNr
     * @return het gezin met nummer nr. Als dat niet bekend is wordt er null
     * geretourneerd
     */
    public Gezin getGezin(int gezinsNr) {
        // aanname: er worden geen gezinnen verwijderd
        try
        {
        if (1 <= gezinsNr && 1 <= gezinnen.size()) {
            return gezinnen.get(gezinsNr - 1);
        }
        return null;
        }
        catch(Exception e)
        {
            return null;
        }
    }
}