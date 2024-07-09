package com.ocrrh.ocr.processing;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class FrenchNamedEntityRecognition {
    private StanfordCoreNLP pipeline;

    public FrenchNamedEntityRecognition() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        props.setProperty("tokenize.language", "fr");
        props.setProperty("ner.applyNumericClassifiers", "false");
        props.setProperty("ner.language", "fr");
        pipeline = new StanfordCoreNLP(props);
    }

    public List<String> identifyEntities(String text) {
        CoreDocument document = new CoreDocument(text);
        pipeline.annotate(document);
        List<String> entities = new ArrayList<>();
        StringBuilder personName = new StringBuilder();

        for (CoreSentence sentence : document.sentences()) {
            for (CoreLabel token : sentence.tokens()) {
                String word = token.word();
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                if ("PERSON".equals(ner)) {
                    if (personName.length() > 0) {
                        personName.append(" ");
                    }
                    personName.append(word);
                } else {
                    if (personName.length() > 0) {
                        entities.add(personName.toString());
                        personName.setLength(0); // Reset the StringBuilder
                    }
                }
            }
            // Add the last person name if the sentence ends with a PERSON entity
            if (personName.length() > 0) {
                entities.add(personName.toString());
                personName.setLength(0);
            }
        }
        return entities;
    }
    public List<String> cleaningEntities(List<String> entities){
        return entities.stream()
                .map(s -> s.replaceFirst("^(Monsieur|Mme)\\s+", ""))
                .collect(Collectors.toList());
    }


}

