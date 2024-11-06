import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.taller3_firebase.MapaActivity
import com.example.taller3_firebase.Usuario
import com.example.taller3_firebase.databinding.UsuarioAdapterBinding

class UsuarioAdapter(context: Context, private val usuarios: List<Usuario>): ArrayAdapter<Usuario>(context, 0, usuarios) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: UsuarioAdapterBinding
        val view: View
        if (convertView == null) {
            binding = UsuarioAdapterBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as UsuarioAdapterBinding
            view = convertView
        }

        val usuario = usuarios[position]
        binding.nombre.text = usuario.nombre
        binding.email.text = usuario.correo
        binding.disponible.text = usuario.disponible.toString()

        view.setOnClickListener {
            val intent = Intent(context, MapaActivity::class.java)
            intent.putExtra("mail", usuario.correo)
            context.startActivity(intent)
        }

        return view
    }
}
