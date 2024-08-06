package com.ocrrh.ocr.docClassification;

import com.ocrrh.ocr.exceptions.DocumentNotClassifiedException;

import java.util.List;

public class DocClassifier {
    public static String findStringsInText(String text) throws DocumentNotClassifiedException {
        List<String[]> docs = List.of(new String[]{"Diplôme", "diplome"},new String[]{"ATTESTATION DU BACCALAUREAT", "bacalaureat"},new String[]{"CARTE NATIONALE D'IDENTITE", "cin"},new String[]{"AVENANT AU CONTRAT DE TRAVAIL", "avenant"},new String[]{"CONTRAT DE TRAVAIL À DUREE INDETERMINEE", "contract_travail"} );
        for (int i=0; i<docs.size();i++) {
            if (text.contains(docs.get(i)[0])) {
                return docs.get(i)[1];
            }
        }

        throw new DocumentNotClassifiedException();
    }
}
