//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// FuzzyTree.java (com.nestor.cs.tasks.impl.clasf.tree.fuzzy)
// Utworzono 2005-09-06 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.impl.clasf.tree.fuzzy;

import com.nestor.cs.data.def.ReadOnlyDataset;
import com.nestor.cs.tasks.def.LearningContext;
import com.nestor.cs.tasks.def.TreeNode;
import com.nestor.cs.tasks.impl.clasf.tree.AbstractTree;
import com.nestor.cs.util.proc.Controller;
import com.nestor.cs.util.proc.ProcessingException;

public class FuzzyTree extends AbstractTree {

    private AbstractTree tree;

    public FuzzyTree() {
        super();
    }

    @Override
    protected TreeNode createRootNode(ReadOnlyDataset ds) {
        return new FuzzyTreeNode(this.tree.getTreeRoot());
    }

    protected void initFuzzyTree(AbstractTree tree) throws ProcessingException {
        this.tree = tree;
        try {
            this.argCacheResults.setValue(tree.argCacheResults.getValue());
            this.argConvertedDataset.setValue(tree.argConvertedDataset.getValue());
            this.argTrainDataset.setValue(tree.argTrainDataset.getValue());
            this.argPruner.setValue(null);
        } catch (Exception e) {
            throw new Error(e);
        }
        setLearningContext(new LearningContext(getArgTrainingDataset()));
        this.root = createRootNode(getArgTrainingDataset());
        Controller.init(this);
        // pomijam step i learn aby unikn�� powt�rnego
        // uczenia drzewa oryginalnego
    }

    @Override
    public void learn(ReadOnlyDataset dsTrain) throws ProcessingException {
        // nie uruchamiam super.learn, gdy� to spowoduje
        // powt�rne nauczenie drzewa rozmytego na
        // starym drzewie oryginalnym. Zamiast tego
        // ucz� nowe drzewo oryginalne i tworz� nowe
        // drzewo rozyte
        setLearningContext(new LearningContext(dsTrain));
        this.tree.learn(dsTrain);
        this.root = createRootNode(dsTrain);
    }
}