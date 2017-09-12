//
// Praca Dyplomowa Magisterska: System Exploracji Danych
// Autor: Nestor Paw�owski
//
// MultiFaceObject.java (sed.data.processing)
// Utworzono 2005-07-23 w �rodowisku Eclipse 3.0
//

package com.nestor.cs.tasks.def;

public interface MultiResultTask {

    public static class TaskResult {

        public static TaskResult[] initElementFaces(Object owner, Object[] objs) {
            TaskResult[] faces = new TaskResult[objs.length];
            for (int i = 0; i < objs.length; i++) {
                faces[i] = new TaskResult(objs[i].getClass().getName(), owner,
                        objs[i]);
            }
            return faces;
        }

        private String elementFaceName;

        private Object elementFaceObject;

        private Object ownerElement;

        public TaskResult(String elementFaceName, Object ownerElement,
                Object elementFaceObject) {
            super();
            this.elementFaceName = elementFaceName;
            this.elementFaceObject = elementFaceObject;
            this.ownerElement = ownerElement;
        }

        public String getElementFaceName() {
            return elementFaceName;
        }

        public Object getElementFaceObject() {
            return elementFaceObject;
        }

        public Object getOwnerElement() {
            return ownerElement;
        }

        public void setElementFaceName(String elementFaceName) {
            this.elementFaceName = elementFaceName;
        }
    }

    public TaskResult[] getTaskResults();

}