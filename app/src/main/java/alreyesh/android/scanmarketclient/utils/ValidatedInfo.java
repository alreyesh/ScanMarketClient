package alreyesh.android.scanmarketclient.utils;

import android.content.Context;
import android.widget.Toast;

public class ValidatedInfo {

    public static Boolean validaty(Context context, String txtNombre, String txtApellidos, String txtCelular, String textSpinner, String numDocumento){

       return ( validate(context, txtNombre, txtApellidos, txtCelular, textSpinner, numDocumento))?true:false;

    }
    private static Boolean validate(Context context, String txtNombre, String txtApellidos, String txtCelular, String textSpinner, String numDocumento) {

         return (!isValidName(context,txtNombre)||!isValidLastName(context,txtApellidos)||!isValidDocument(context,textSpinner,numDocumento))?false:true;
    }

    public static boolean isValidCellPhone(Context context, String txtCelular) {
        boolean integerOrNot = txtCelular.matches("-?\\d+");
        if(integerOrNot){
            int number =  txtCelular.length();
            if(number == 9){
                return true;
            }else{
                Toast.makeText(context, "Ingrese un numero de celular de 9 digitos", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(context, "Ingrese un numero de celular de 9 digitos", Toast.LENGTH_SHORT).show();

        }

        return false;
    }

    public static boolean isValidLastName(Context context, String txtApellidos) {
        int contar=  contarCaracteres(txtApellidos,' ');
        boolean stringOrNot = txtApellidos.matches("^[a-zA-Z]*$");
        if(contar>1){
            Toast.makeText(context, "Ingrese correctamente su apellidos: Deje espacio entre sus dos apellidos", Toast.LENGTH_SHORT).show();
            return false;
        }else if(Boolean.FALSE.equals(stringOrNot)){
            Toast.makeText(context, "Ingrese correctamente sus apellidos", Toast.LENGTH_SHORT).show();

        }

        return true;
    }

    public static boolean isValidName(Context context, String txtNombre) {
        int contar=  contarCaracteres(txtNombre,' ');
        boolean stringOrNot = txtNombre.matches("^[a-zA-Z]*$");
        if(contar>1){
            Toast.makeText(context, "Ingrese correctamente su nombre: Deje espacio entre sus dos nombres", Toast.LENGTH_SHORT).show();
            return false;
        }else if(Boolean.FALSE.equals(stringOrNot)){
            Toast.makeText(context, "Ingrese correctamente su nombre", Toast.LENGTH_SHORT).show();

        }

        return true;
    }

    public static boolean isValidDocument(Context context, String textSpinner, String numDocumento) {
        int number =  numDocumento.length();
        boolean integerOrNot = numDocumento.matches("-?\\d+");
        if (integerOrNot)
        {
            switch (textSpinner){
                case "DNI":
                    if(number==8){
                        return true;
                    }else{
                        Toast.makeText(context, "Digite correctamente su numero de DNI (8 dig.)", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                case "RUC":
                    if(number==11){
                        return true;
                    }else{
                        Toast.makeText(context, "Digite correctamente su numero de RUC(11 dig.)", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                case "CEX":
                    if(number==12){
                        return true;
                    }else{
                        Toast.makeText(context, "Digite correctamente su numero de Carnet de Extranjeria(12 dig.)", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                case "Pasaporte":
                    if(number==12){
                        return true;
                    }else{
                        Toast.makeText(context, "Digite correctamente su numero de Pasaporte (12 dig.)", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                default:
                    Toast.makeText(context, "Seleccione correctamente un documento de Identidad", Toast.LENGTH_SHORT).show();
                    return false;
            }
        }else{
            Toast.makeText(context, "Digite correctamente su numero de documento", Toast.LENGTH_SHORT).show();
        }

        return false;
    }


    public static int contarCaracteres(String cadena, char caracter) {
        int posicion;
        int contador = 0;
        //se busca la primera vez que aparece
        posicion = cadena.indexOf(caracter);
        while (posicion != -1) { //mientras se encuentre el caracter
            contador++;           //se cuenta
            //se sigue buscando a partir de la posición siguiente a la encontrada
            posicion = cadena.indexOf(caracter, posicion + 1);
        }
        return contador;
    }
}
