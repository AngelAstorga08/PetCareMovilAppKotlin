package com.example.petcaremovilapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.example.petcaremovilapp.data.repository.ClientRepository
import com.example.petcaremovilapp.models.dto.ClientResponseDto
import com.example.petcaremovilapp.models.entities.Client
import kotlinx.coroutines.launch
import com.example.petcaremovilapp.models.dto.Result

class ClientViewModel : ViewModel() {

    private val repository = ClientRepository()

    // Clientes
    private val _clientes = MutableLiveData<Result<List<Client>>>()
    val clientes: LiveData<Result<List<Client>>> = _clientes

    // Cliente individual
    private val _clienteDetalle = MutableLiveData<Result<Client>>()
    val clienteDetalle: LiveData<Result<Client>> = _clienteDetalle

    // Crear cliente
    private val _createResult = MutableLiveData<Result<ClientResponseDto>>()
    val createResult: LiveData<Result<ClientResponseDto>> = _createResult

    // actualizar cliente
    private val _updateResult = MutableLiveData<Result<ClientResponseDto>>()
    val updateResult: LiveData<Result<ClientResponseDto>> = _updateResult

    // eliminar cliente
    private val _deleteResult = MutableLiveData<Result<Client>>()
    val deleteResult: LiveData<Result<Client>> = _deleteResult

    // --- Funciones que llama la UI ---

    fun fetchAllClients() {
        _clientes.value = Result.Loading
        viewModelScope.launch {
            _clientes.value = repository.getAllClientes()
        }
    }

    fun fetchClientByClave(clave: String) {
        _clienteDetalle.value = Result.Loading
        viewModelScope.launch {
            _clienteDetalle.value = repository.getClientByClave(clave)
        }
    }
    fun updateClient(client: Client) {
        _updateResult.value = Result.Loading
        viewModelScope.launch {
            _updateResult.value = repository.updateClient(client)
        }
    }
    fun createClient(client: Client) {
        _createResult.value = Result.Loading
        viewModelScope.launch {
            _createResult.value = repository.createClient(client)
        }
    }
    fun deleteClient(clave: String) {
        _deleteResult.value = Result.Loading
        viewModelScope.launch {
            _deleteResult.value = repository.deleteClient(clave)
        }
    }

}